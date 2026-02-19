package com.example.backend_jovyweb.modules.auth.service;

import com.example.backend_jovyweb.modules.auth.dto.LoginResponse;
import com.example.backend_jovyweb.modules.auth.dto.LogoutResponse;
import com.example.backend_jovyweb.modules.auth.model.User;
import com.example.backend_jovyweb.modules.auth.model.AuditLog;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Interfaz del servicio de autenticación.
 * Define los métodos para login, logout y validación de sesión.
 */
public interface AuthService {

    /**
     * Realiza el login del usuario usando el script PowerShell.
     *
     * @param username  nombre de usuario o código de empleado
     * @param password  contraseña
     * @param ipAddress dirección IP del cliente
     * @return usuario autenticado
     * @throws RuntimeException si las credenciales son inválidas o no tiene
     *                          permisos
     */
    User login(String username, String password, String ipAddress);

    /**
     * Maneja el login del usuario y configura la respuesta HTTP con cookies.
     * Este método encapsula toda la lógica de generación de respuesta e incluye:
     * - Autenticación del usuario
     * - Generación de JWT y Refresh Token
     * - Configuración de cookies HttpOnly/Secure
     * - Respuesta con datos del usuario
     *
     * @param username  nombre de usuario o código de empleado
     * @param password  contraseña
     * @param ipAddress dirección IP del cliente
     * @param response  respuesta HTTP para configurar cookies
     * @return LoginResponse con datos del usuario y token
     * @throws RuntimeException si las credenciales son inválidas
     */
    LoginResponse handleLogin(String username, String password, String ipAddress, HttpServletResponse response);

    /**
     * Realiza el logout del usuario invalidando su sesión.
     *
     * @param jwtToken token JWT del usuario
     * @return true si el logout fue exitoso
     */
    boolean logout(String jwtToken);

    /**
     * Maneja el logout del usuario y limpia las cookies.
     * Este método encapsula:
     * - Validación del token
     * - Invalidación del token
     * - Limpieza de sesión
     * - Configuración de cookie removida
     *
     * @param jwtToken token JWT del usuario
     * @param response respuesta HTTP para limpiar cookies
     * @return LogoutResponse con resultado
     */
    LogoutResponse handleLogout(String jwtToken, HttpServletResponse response);

    /**
     * Valida si un token JWT es válido y pertence a un usuario activo.
     *
     * @param jwtToken token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String jwtToken);

    /**
     * Obtiene un usuario por su token JWT.
     *
     * @param jwtToken token JWT
     * @return usuario si es válido, Optional.empty() si no
     */
    Optional<User> getUserByToken(String jwtToken);

    /**
     * Obtiene un usuario por su código de empleado.
     * Este método busca en sesión, no en BD.
     *
     * @param employeeCode código del empleado
     * @return usuario si existe en sesión, Optional.empty() si no
     */
    Optional<User> getUserByEmployeeCode(Integer employeeCode);

    /**
     * Registra una entrada de auditoría en la base de datos.
     *
     * @param auditLog registro de auditoría a guardar
     * @return registro de auditoría guardado
     */
    AuditLog saveAuditLog(AuditLog auditLog);

    /**
     * Limpia la sesión del usuario (al cerrar navegador o cambiar región).
     *
     * @param employeeCode código del empleado
     */
    void clearUserSession(Integer employeeCode);

    /**
     * Renueva el JWT usando un Refresh Token.
     * Valida que el Refresh Token sea válido (no expirado).
     * Genera un nuevo JWT de 15 minutos.
     *
     * @param refreshToken token de renovación (válido por 7 días)
     * @return usuario con nuevo JWT, o null si el refresh token es inválido
     * @throws RuntimeException si el refresh token está inválido o expirado
     */
    User refresh(String refreshToken);
}
