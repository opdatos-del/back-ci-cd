package com.example.backend_jovyweb.modules.auth.controller;

import com.example.backend_jovyweb.modules.auth.dto.LoginRequest;
import com.example.backend_jovyweb.modules.auth.dto.LoginResponse;
import com.example.backend_jovyweb.modules.auth.dto.LogoutResponse;
import com.example.backend_jovyweb.modules.auth.service.AuthService;
import com.example.backend_jovyweb.modules.auth.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador REST para autenticación de usuarios.
 * Endpoints para login y logout.
 * El token se envía como cookie HttpOnly/Secure, no se almacena en el cliente.
 */
/**
 * Controlador REST para autenticación de usuarios.
 * Endpoints para login y logout.
 * El token se envía como cookie HttpOnly/Secure, no se almacena en el cliente.
 * 
 * Responsabilidades:
 * - Recibir peticiones HTTP
 * - Delegar lógica a servicios
 * - Retornar respuestas HTTP con códigos apropiados
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    /**
     * Endpoint de login.
     * 
     * POST /api/auth/login
     * 
     * Response:
     * - 200 OK con datos del usuario y cookie HttpOnly
     * - 401 Unauthorized si las credenciales son inválidas
     * - 403 Forbidden si el usuario no tiene permisos
     */
    /**
     * Endpoint de login.
     * Delega la lógica completamente al servicio.
     * 
     * @param loginRequest credenciales del usuario
     * @param request      petición HTTP
     * @param response     respuesta HTTP
     * @return objeto con LoginResponse y cookie configurada
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            // Validar que los parámetros no sean null o vacíos
            if (loginRequest == null ||
                    loginRequest.getVlogUsername() == null || loginRequest.getVlogUsername().isEmpty()) {

                LoginResponse errorResponse = new LoginResponse(
                        null, null, null, null, null, null,
                        "Usuario y contraseña son requeridos",
                        false);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            String ipAddress = httpRequestUtil.getClientIpAddress(request);

            // El servicio retorna la respuesta completa con token listo
            LoginResponse loginResponse = authService.handleLogin(
                    loginRequest.getVlogUsername(),
                    loginRequest.getVlogPassword(),
                    ipAddress,
                    response);

            return ResponseEntity.ok(loginResponse);

        } catch (RuntimeException e) {
            logger.error("Error en login: {}", e.getMessage());

            LoginResponse errorResponse = new LoginResponse(
                    null, null, null, null, null, null,
                    "Error de autenticación: " + e.getMessage(),
                    false);

            if (e.getMessage().contains("no tiene permisos")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint de logout.
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            String token = httpRequestUtil.extractTokenFromRequest(request);

            if (token == null) {
                logger.warn("Intento de logout sin token - IP: {}", httpRequestUtil.getClientIpAddress(request));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LogoutResponse("Token no proporcionado", false));
            }

            // El servicio maneja logout y limpieza de cookie
            LogoutResponse logoutResponse = authService.handleLogout(token, response);

            if (logoutResponse.isSuccess()) {
                return ResponseEntity.ok(logoutResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(logoutResponse);
            }

        } catch (Exception e) {
            logger.error("Error inesperado en logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse("Error interno del servidor: " + e.getMessage(), false));
        }
    }

    /**
     * Endpoint para validar si el usuario tiene sesión activa.
     * 
     * GET /api/auth/validate
     * Headers: Cookie con X-AUTH-TOKEN o Authorization Bearer token
     * 
     * Response:
     * - 200 OK si la sesión es válida
     * - 401 Unauthorized si la sesión no es válida
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {
        try {
            String token = httpRequestUtil.extractTokenFromRequest(request);

            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LogoutResponse("Sesión inválida o expirada", false));
            }

            return ResponseEntity.ok(new LogoutResponse("Sesión activa", true));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LogoutResponse("Error al validar sesión", false));
        }
    }

}
