package com.example.backend_jovyweb.modules.auth.interceptor;

import com.example.backend_jovyweb.modules.auth.service.AuthService;
import com.example.backend_jovyweb.modules.auth.util.DeviceFingerprintUtil;
import com.example.backend_jovyweb.modules.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor para validar tokens JWT en cada petición.
 * 
 * Validaciones de seguridad:
 * - Token JWT válido y no expirado
 * - Device Fingerprint coincide (IP + User-Agent)
 * - Detección de cambios de dispositivo
 * - Si el token es inválido, retorna 401 Unauthorized
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    private static final String TOKEN_COOKIE_NAME = "X-AUTH-TOKEN";
    private static final String[] EXCLUDED_PATHS = {
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/validate",
            "/swagger-ui",
            "/v3/api-docs",
            "/h2-console"
    };

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private DeviceFingerprintUtil deviceFingerprintUtil;

    @Value("${auth.device.validation:false}")
    private boolean deviceValidationEnabled;

    @Value("${auth.device.changed.action:WARN}")
    private String deviceChangedAction; // WARN o BLOCK

    /**
     * Se ejecuta antes de que se procese la petición.
     * Valida el token JWT y device fingerprint.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestPath = request.getRequestURI();

        // Excluir rutas públicas
        if (isPathExcluded(requestPath)) {
            return true;
        }

        // Extraer el token de la cookie o del header
        String token = extractTokenFromRequest(request);

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token no encontrado\"}");
            return false;
        }

        // Validar el token
        if (!authService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            return false;
        }

        // ✅ REFRESH AUTOMÁTICO: Verificar si el token está próximo a expirar
        // Si está a punto de expirar (< 2 minutos), hace refresh automático
        if (jwtUtil.isTokenNearExpiration(token)) {
            try {
                // Obtener el refresh token desde la cookie
                String refreshToken = extractRefreshTokenFromCookie(request);

                if (refreshToken != null) {
                    // Intentar refrescar el token
                    authService.refresh(refreshToken);

                    // El nuevo token está en la sesión, extraerlo
                    String newToken = authService.getUserByEmployeeCode(
                            jwtUtil.getEmployeeCodeFromToken(token))
                            .map(user -> user.getToken())
                            .orElse(null);

                    if (newToken != null) {
                        // Actualizar la cookie con el nuevo token
                        addNewTokenCookie(response, newToken);

                        // Usar el nuevo token para el resto de la petición
                        request.setAttribute("token", newToken);
                        token = newToken;
                    }
                }
            } catch (Exception e) {
                // El refresh falló, pero si el token actual es aún válido, permitir la petición
                // El siguiente acceso necesitará relogin
                logger.warn("Error al hacer refresh automático: {}", e.getMessage());
            }
        }

        // Validar device fingerprint si está habilitado
        if (deviceValidationEnabled) {
            if (!validateDeviceFingerprint(request, token, response)) {
                return false; // La validación fallió, response ya está configurada
            }
        }

        // Agregar información del usuario al request
        Integer employeeCode = jwtUtil.getEmployeeCodeFromToken(token);

        request.setAttribute("employeeCode", employeeCode);
        request.setAttribute("token", token);

        // ⚠️ departmentCode y deviceFingerprint YA NO están en JWT por razones de
        // seguridad
        // - departmentCode: Debe consultarse en BD si es necesario
        // - deviceFingerprint: Se validó en el paso anterior
        // (validateDeviceFingerprint)
        request.setAttribute("departmentCode", null); // Consultar en BD si se necesita
        request.setAttribute("deviceFingerprint", null); // Validado pero no almacenado en request

        // ...existing code...

        return true;
    }

    /**
     * Valida que el device fingerprint coincida.
     * Detecta si el token se intenta usar desde otro dispositivo.
     * 
     * El deviceFingerprint debe enviarse en el header X-Device-Fingerprint
     * (generado durante el login y almacenado en el cliente).
     *
     * @return true si el fingerprint es válido, false si hay anomalía
     */
    private boolean validateDeviceFingerprint(HttpServletRequest request, String token, HttpServletResponse response)
            throws Exception {
        try {
            // Extraer el deviceFingerprint del header
            String clientFingerprint = request.getHeader("X-Device-Fingerprint");

            // Si falta el header, simplemente ignorar (permitir la petición sin device
            // fingerprint)
            // Esto es importante para herramientas como Swagger UI que no envían este
            // header
            if (clientFingerprint == null || clientFingerprint.isBlank()) {
                logger.debug(
                        "⚠️  Header X-Device-Fingerprint no encontrado. Permitiendo petición (sin validación de dispositivo)");
                return true; // Permitir la petición aunque no tenga device fingerprint
            }

            // Calcular fingerprint actual del dispositivo
            String currentIp = deviceFingerprintUtil.extractClientIp(request);
            String currentUserAgent = deviceFingerprintUtil.extractUserAgent(request);
            String currentFingerprint = deviceFingerprintUtil.generateFingerprint(currentIp, currentUserAgent);

            // Validar que coincidan
            if (!currentFingerprint.equals(clientFingerprint)) {
                if ("BLOCK".equals(deviceChangedAction)) {
                    logger.error("❌ Bloqueando petición: Fingerprint no coincide (posible token robado)");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Dispositivo no reconocido (fingerprint inválido)\"}");
                    return false;
                } else {
                    logger.warn("⚠️  Fingerprint no coincide pero permitiendo (action=WARN)");
                }
            }

            return true;

        } catch (Exception e) {
            logger.error("❌ Error al validar device fingerprint", e);
            // En caso de error, permitir la petición en lugar de bloquearla
            logger.warn("Permitiendo petición a pesar de error en device fingerprint validation");
            return true;
        }
    }

    /**
     * Extrae el token de la cookie o del header Authorization.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Intenta obtener el token de la cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Si no está en cookie, intenta del header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Verifica si una ruta está excluida de validación de token.
     */
    private boolean isPathExcluded(String path) {
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extrae el refresh token desde las cookies.
     * El refresh token se usa para renovar el JWT automáticamente.
     *
     * @param request petición HTTP
     * @return refresh token si existe, null en caso contrario
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-REFRESH-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Agrega el nuevo token JWT a la respuesta como cookie HttpOnly/Secure.
     * Se usa cuando se hace refresh automático del token.
     *
     * @param response respuesta HTTP
     * @param newToken nuevo token JWT
     */
    private void addNewTokenCookie(HttpServletResponse response, String newToken) {
        ResponseCookie cookie = ResponseCookie
                .from(TOKEN_COOKIE_NAME, newToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(900) // 15 minutos (mismo que la expiración del JWT)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
