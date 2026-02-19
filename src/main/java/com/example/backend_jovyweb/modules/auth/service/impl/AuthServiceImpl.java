package com.example.backend_jovyweb.modules.auth.service.impl;

import com.example.backend_jovyweb.modules.auth.dto.LoginResponse;
import com.example.backend_jovyweb.modules.auth.dto.LogoutResponse;
import com.example.backend_jovyweb.modules.auth.model.User;
import com.example.backend_jovyweb.modules.auth.model.AuditLog;
import com.example.backend_jovyweb.modules.auth.repository.AuditLogRepository;
import com.example.backend_jovyweb.modules.auth.service.AuthService;
import com.example.backend_jovyweb.modules.auth.util.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del servicio de autenticación.
 * Gestiona login, logout, validación de tokens y sesiones de usuario.
 * 
 * Mejoras de seguridad:
 * - Validación de deviceFingerprint (deteccion de cambios de dispositivo)
 * - Rate Limiting en login (prevención de fuerza bruta)
 * - JWT con expiración corta (15 minutos)
 * - Refresh Tokens para renovación sin relogin
 * - Auditoría completa de acciones
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    // Almacenamiento en memoria de sesiones activas: token -> User
    private final Map<String, User> activeSessions = new ConcurrentHashMap<>();

    // Almacenamiento de tokens inválidos (para logout): token -> timestamp
    private final Map<String, LocalDateTime> invalidatedTokens = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenUtil refreshTokenUtil;

    @Autowired
    private DeviceFingerprintUtil deviceFingerprintUtil;

    @Autowired
    private RateLimiterUtil rateLimiterUtil;

    @Autowired
    private AuthenticationUtil authUtil;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    /**
     * Realiza el login del usuario con validaciones de seguridad mejoradas.
     * 
     * 1. Validar Rate Limiting (prevenir fuerza bruta)
     * 2. Ejecutar script PowerShell de autenticación
     * 3. Validar permisos de acceso
     * 4. Generar device fingerprint
     * 5. Generar JWT y Refresh Token
     * 6. Almacenar sesión
     * 7. Registrar auditoría
     */
    @Override
    public User login(String username, String password, String ipAddress) {
        // Validar y normalizar la IP
        ipAddress = normalizeIpAddress(ipAddress);

        try {
            // 1. Validar Rate Limiting
            if (rateLimiterUtil.isBlocked(ipAddress)) {
                LocalDateTime unblockTime = rateLimiterUtil.getUnblockTime(ipAddress);
                logger.warn("IP bloqueada por Rate Limiting: {} hasta {}", ipAddress, unblockTime);
                throw new RuntimeException("Demasiados intentos fallidos. Intenta más tarde.");
            }

            // 2. Ejecutar autenticación
            Map<String, Object> authResponse = authUtil.authenticate(username, password);

            // 3. Validar que tenga permisos de acceso
            if (!authUtil.hasAccessPermission(authResponse)) {
                logFailedLoginAttempt(username, ipAddress, "Sin permisos de acceso");
                rateLimiterUtil.recordFailedAttempt(ipAddress); // Contar intento fallido
                logger.warn("Usuario {} no tiene permisos de acceso", username);
                throw new RuntimeException("El usuario no tiene permisos para acceder al sistema");
            }

            // 4. Extraer datos del usuario
            Integer employeeCode = authUtil.extractEmployeeCode(authResponse);
            String employeeIdFromSp = String.valueOf(employeeCode != null ? employeeCode : "");

            // Usar el valor del SP directamente, sin fallback ni hash
            if (employeeCode == null) {
                employeeCode = 0; // Convertir null a 0
            }

            Integer departmentCode = authUtil.extractDepartmentCode(authResponse);
            if (departmentCode == null) {
                departmentCode = 0; // Fallback si Department no está disponible
            }
            String name = (String) authResponse.get("Name");
            String email = (String) authResponse.get("Email");
            String slpCode = (String) authResponse.get("SlpCode");

            // 5. Generar device fingerprint (necesario para validar cambios de dispositivo)
            String userAgent = "Unknown"; // Se obtiene del interceptor en una petición real
            String deviceFingerprint = deviceFingerprintUtil.generateFingerprint(ipAddress, userAgent);

            // 6. Generar JWT con device fingerprint (SIN ps1Token en el JWT)
            String jwtToken = jwtUtil.generateJWT(employeeCode, departmentCode, email, slpCode, deviceFingerprint);

            // 7. Generar Refresh Token
            String refreshToken = refreshTokenUtil.generateRefreshToken(employeeCode, deviceFingerprint);

            // 8. Crear objeto User y almacenar en sesión
            // accessNumber: Si Status=1 (autenticado), usamos 1 por defecto
            Integer accessNumber = authUtil.hasAccessPermission(authResponse) ? 1 : 0;
            User user = new User(employeeCode, name, email, jwtToken, departmentCode,
                    accessNumber, slpCode, deviceFingerprint);
            user.setRefreshToken(refreshToken); // Guardar temporalmente para retornar en login
            activeSessions.put(jwtToken, user);

            // 9. Limpiar intentos fallidos (login exitoso)
            rateLimiterUtil.clearAttempts(ipAddress);

            // 10. Registrar auditoría (usar EmployeeID del SP si está disponible)
            logSuccessfulLogin(employeeCode, email, ipAddress, employeeIdFromSp);

            return user;

        } catch (RuntimeException e) {
            // Registrar intento fallido
            logger.error("Error en login para {}: {}", username, e.getMessage());
            rateLimiterUtil.recordFailedAttempt(ipAddress); // Contar intento fallido
            throw e;
        }
    }

    /**
     * Realiza el logout del usuario.
     * NOTA: Invalida TODOS los tokens del usuario en TODOS sus
     * dispositivos/navegadores.
     * 
     * Pasos:
     * 1. Buscar sesión activa del token
     * 2. Si no existe, fallar (token ya inválido o falsificado)
     * 3. Extraer información del empleado
     * 4. Buscar TODOS los tokens activos de este usuario
     * 5. Invalidar TODOS esos tokens (agregar a blacklist)
     * 6. Eliminar TODAS sus sesiones activas
     * 7. Registrar auditoría
     */
    @Override
    public boolean logout(String jwtToken) {
        try {
            // PASO 1: Buscar sesión activa usando el token como clave
            User userSession = activeSessions.get(jwtToken);

            if (userSession == null) {
                return false;
            }

            // PASO 2: Extraer información del usuario directamente de la sesión (más seguro
            // que del token)
            Integer employeeCode = userSession.getEmployeeCode();
            String email = userSession.getEmail();
            String deviceFingerprint = userSession.getDeviceFingerprint();

            // PASO 3: Verificar integridad del token (que la firma sea válida)
            // pero sin chequear invalidatedTokens (porque aún no está invalidado)
            if (!jwtUtil.validateToken(jwtToken)) {
                // Aún así intentamos limpiar la sesión
            }

            // PASO 4: Buscar TODOS los tokens activos de este usuario
            List<String> userTokens = activeSessions.entrySet().stream()
                    .filter(entry -> entry.getValue().getEmployeeCode().equals(employeeCode))
                    .map(Map.Entry::getKey)
                    .toList();

            // PASO 5: Invalidar TODOS los tokens (agregar a blacklist)
            userTokens.forEach(token -> {
                invalidatedTokens.put(token, LocalDateTime.now());
            });

            // PASO 6: Eliminar TODAS las sesiones activas del usuario
            long removedSessions = activeSessions.entrySet().stream()
                    .filter(entry -> entry.getValue().getEmployeeCode().equals(employeeCode))
                    .peek(entry -> {
                        entry.getValue().setActive(false);
                    })
                    .map(Map.Entry::getKey)
                    .peek(activeSessions::remove)
                    .count();

            // PASO 7: Registrar auditoría
            String logoutDescription = "logout (todos los dispositivos - " + userTokens.size() + " sesiones)";
            AuditLog auditLog = new AuditLog(
                    employeeCode,
                    "LOGOUT",
                    "AUTH",
                    employeeCode.toString(),
                    "", // previousValues - vacío, no había valores previos
                    logoutDescription, // newValues - descripción del logout
                    email, // ipAddress - email del usuario
                    1);
            saveAuditLog(auditLog);

            return true;

        } catch (RuntimeException e) {
            logger.error("Error crítico en logout: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado en logout: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Renueva el JWT usando un Refresh Token.
     * 
     * Pasos:
     * 1. Validar que el refresh token sea válido (no expirado)
     * 2. Extraer información del refresh token (employeeCode, deviceFingerprint)
     * 3. Buscar sesión activa del usuario
     * 4. Generar nuevo JWT (15 minutos)
     * 5. Actualizar sesión con nuevo JWT
     * 6. Retornar usuario con nuevo token
     * 
     * @param refreshToken token de renovación válido por 7 días
     * @return usuario con nuevo JWT, o null si el refresh token es inválido
     * @throws RuntimeException si el refresh token está inválido o expirado
     */
    @Override
    public User refresh(String refreshToken) {
        try {

            // 1. Validar que el refresh token sea válido
            if (!refreshTokenUtil.validateRefreshToken(refreshToken)) {
                throw new RuntimeException("Refresh Token inválido o expirado. Debe hacer login nuevamente.");
            }

            // 2. Extraer información del refresh token
            Integer employeeCode = refreshTokenUtil.getEmployeeCodeFromRefreshToken(refreshToken);
            String deviceFingerprint = refreshTokenUtil.getDeviceFingerprintFromRefreshToken(refreshToken);

            // 3. Buscar sesión activa del usuario
            Optional<User> userSessionOpt = getUserByEmployeeCode(employeeCode);
            if (userSessionOpt.isEmpty()) {
                throw new RuntimeException("No hay sesión activa. Debe hacer login nuevamente.");
            }

            User userSession = userSessionOpt.get();

            // Validar que el device fingerprint coincida (seguridad adicional)
            if (!userSession.getDeviceFingerprint().equals(deviceFingerprint)) {
                logger.warn("Device fingerprint no coincide - Posible intento de robo de token");
                logger.warn("   Esperado: {}", userSession.getDeviceFingerprint());
                logger.warn("   Recibido: {}", deviceFingerprint);
                throw new RuntimeException("Device fingerprint no coincide. Acceso denegado.");
            }

            // 4. Generar nuevo JWT (15 minutos)
            String newJwtToken = jwtUtil.generateJWT(employeeCode, userSession.getDepartmentCode(),
                    userSession.getEmail(), userSession.getSlpCode(), deviceFingerprint);

            // 5. Remover token anterior de sesiones activas y agregar el nuevo
            String oldToken = userSession.getToken();
            activeSessions.remove(oldToken);

            // Invalidar el token anterior (aunque sea válido, ya no se usará)
            invalidatedTokens.put(oldToken, LocalDateTime.now());

            // Actualizar usuario con nuevo token
            userSession.setToken(newJwtToken);
            userSession.setLoginTime(LocalDateTime.now());
            activeSessions.put(newJwtToken, userSession);

            // 6. NO registrar auditoría en refresh para evitar llenar la tabla
            // Cada refresh de token genera demasiadas auditorías innecesariamente
            // Solo registrar LOGIN y LOGOUT en la auditoría
            // AuditLog auditLog = new AuditLog(
            // employeeCode,
            // "REFRESH",
            // "AUTH",
            // employeeCode.toString(),
            // "",
            // "Token JWT renovado automáticamente",
            // 1);
            // saveAuditLog(auditLog);

            return userSession;

        } catch (RuntimeException e) {
            logger.error("Error en refresh de token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en refresh: {}", e.getMessage(), e);
            throw new RuntimeException("Error al renovar el token", e);
        }
    }

    /**
     * Valida si un token JWT es válido y puede ser usado.
     * 
     * Verifica en orden:
     * 1. Token NO esté en la lista negra (invalidatedTokens)
     * 2. Firma criptográfica sea válida
     * 3. NO esté expirado
     * 4. Exista una sesión activa para este token
     * 
     * @param jwtToken token JWT a validar
     * @return true si es válido y puede usarse, false en caso contrario
     */
    @Override
    public boolean validateToken(String jwtToken) {
        // Limpiar tokens expirados de la blacklist periódicamente
        cleanupExpiredInvalidatedTokens();

        // 1. Verificar PRIMERO que no esté en la blacklist
        if (invalidatedTokens.containsKey(jwtToken)) {
            return false;
        }

        // 2. Validar JWT (firma, expiración)
        if (!jwtUtil.validateToken(jwtToken)) {
            return false;
        }

        // 3. Verificar que exista sesión activa para este token
        User userSession = activeSessions.get(jwtToken);
        if (userSession == null) {
            return false;
        }
        return true;
    }

    /**
     * Limpia tokens invalidados que ya han expirado (más de 24 horas invalidados).
     * Previene que el mapa de invalidatedTokens crezca indefinidamente.
     */
    private void cleanupExpiredInvalidatedTokens() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        invalidatedTokens.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoffTime))
                .peek(entry -> invalidatedTokens.remove(entry.getKey()))
                .count();
    }

    /**
     * Obtiene un usuario por su token JWT.
     */
    @Override
    public Optional<User> getUserByToken(String jwtToken) {
        if (validateToken(jwtToken)) {
            return Optional.ofNullable(activeSessions.get(jwtToken));
        }
        return Optional.empty();
    }

    /**
     * Obtiene un usuario por su código de empleado.
     * Busca en las sesiones activas.
     */
    @Override
    public Optional<User> getUserByEmployeeCode(Integer employeeCode) {
        return activeSessions.values().stream()
                .filter(user -> user.getEmployeeCode().equals(employeeCode) && user.isActive())
                .findFirst();
    }

    /**
     * Guarda un registro de auditoría en la base de datos.
     */
    @Override
    public AuditLog saveAuditLog(AuditLog auditLog) {
        try {
            AuditLog saved = auditLogRepository.save(auditLog);
            return saved;
        } catch (Exception e) {
            logger.error("Error al guardar registro de auditoría", e);
            return auditLog;
        }
    }

    /**
     * Limpia la sesión de un usuario.
     */
    @Override
    public void clearUserSession(Integer employeeCode) {
        activeSessions.values().stream()
                .filter(user -> user.getEmployeeCode().equals(employeeCode))
                .forEach(user -> {
                    activeSessions.remove(user.getToken());
                    user.setActive(false);
                });
    }

    /**
     * Registra un intento de login exitoso en auditoría.
     */
    private void logSuccessfulLogin(Integer employeeCode, String email, String ipAddress, String employeeIdFromSp) {
        // Usar empleeeCode directamente del SP
        String referenceId = String.valueOf(employeeCode != null ? employeeCode : "");

        AuditLog auditLog = new AuditLog(
                employeeCode,
                "LOGIN",
                "AUTH",
                referenceId,
                "", // previousValues - vacío
                "Login exitoso desde " + ipAddress, // newValues - descripción
                ipAddress,
                1);
        saveAuditLog(auditLog);
    }

    /**
     * Registra un intento de login fallido en auditoría.
     */
    private void logFailedLoginAttempt(String username, String ipAddress, String reason) {
        logger.warn("Intento de login fallido para usuario: {} desde: {} - Motivo: {}",
                username, ipAddress, reason);
        // En el futuro, guardar en una tabla adicional de intentos fallidos
    }

    /**
     * Normaliza y valida la dirección IP.
     * 
     * Reglas:
     * 1. Si es null, retorna "0.0.0.0 (desconocida)"
     * 2. Si es localhost (127.0.0.1 o ::1), lo deja como está
     * 3. Si contiene patrones inválidos (0:0:0:0, ::, etc), retorna "0.0.0.0
     * (desconocida)"
     * 4. Si es una IP válida, la retorna sin cambios
     * 
     * @param ipAddress IP a normalizar
     * @return IP normalizada y validada
     */
    private String normalizeIpAddress(String ipAddress) {
        // 1. Validar null
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return "0.0.0.0 (desconocida)";
        }

        ipAddress = ipAddress.trim();

        // 2. Validar patrones IPv4 inválidos (0.0.0.0, etc)
        if (ipAddress.equals("0.0.0.0") || ipAddress.contains("0:0:0:0") || ipAddress.equals("::")) {
            return "0.0.0.0 (desconocida)";
        }

        // 3. Localhost IPv4 - aceptable en desarrollo
        if (ipAddress.equals("127.0.0.1")) {
            return "127.0.0.1 (localhost)";
        }

        // 4. Localhost IPv6 - aceptable en desarrollo
        if (ipAddress.equals("::1")) {
            return "::1 (localhost)";
        }

        // 5. Si llegó aquí, parece ser una IP válida
        return ipAddress;
    }

    /**
     * Maneja el login del usuario y configura la respuesta HTTP con cookies.
     * Encapsula toda la lógica de login y generación de respuesta.
     *
     * @param username  nombre de usuario
     * @param password  contraseña
     * @param ipAddress dirección IP del cliente
     * @param response  respuesta HTTP para configurar cookies
     * @return LoginResponse con datos del usuario
     */
    @Override
    public LoginResponse handleLogin(String username, String password, String ipAddress, HttpServletResponse response) {
        try {
            // Realizar autenticación
            User authenticatedUser = login(username, password, ipAddress);

            // Crear respuesta con datos del usuario
            LoginResponse loginResponse = new LoginResponse(
                    authenticatedUser.getEmployeeCode(),
                    authenticatedUser.getName(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getDepartmentCode(),
                    authenticatedUser.getAccessNumber(),
                    authenticatedUser.getSlpCode(),
                    "Login exitoso",
                    true);

            // Configurar cookie HttpOnly/Secure con el token
            ResponseCookie cookie = ResponseCookie
                    .from(httpRequestUtil.getTokenCookieName(), authenticatedUser.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(86400)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return loginResponse;

        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * Maneja el logout del usuario y limpia las cookies.
     *
     * @param jwtToken token JWT del usuario
     * @param response respuesta HTTP para limpiar cookies
     * @return LogoutResponse con resultado
     */
    @Override
    public LogoutResponse handleLogout(String jwtToken, HttpServletResponse response) {
        try {
            // Validar que el token sea válido
            if (!validateToken(jwtToken)) {
                logger.warn("Intento de logout con token inválido");
                addCookieRemovalHeader(response);
                return new LogoutResponse("Token inválido o expirado. Sesión ya cerrada.", false);
            }

            // Realizar logout (invalida token)
            boolean logoutSuccess = logout(jwtToken);

            if (logoutSuccess) {
                logger.warn("Logout exitoso - Token: {}...", jwtToken.substring(0, 20));
                addCookieRemovalHeader(response);
                return new LogoutResponse("Cierre de Sesión Exitoso.", true);
            } else {
                logger.error("Logout falló para token: {}...", jwtToken.substring(0, 20));
                addCookieRemovalHeader(response);
                return new LogoutResponse("Error al procesar logout. Sesión puede no estar activa.", false);
            }

        } catch (Exception e) {
            logger.error("Error en handleLogout: {}", e.getMessage(), e);
            addCookieRemovalHeader(response);
            return new LogoutResponse("Error al procesar logout", false);
        }
    }

    /**
     * Helper: Agrega header Set-Cookie para eliminar la cookie de token.
     */
    private void addCookieRemovalHeader(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from(httpRequestUtil.getTokenCookieName(), "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
