package com.example.backend_jovyweb.modules.auth.util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilidad para operaciones HTTP y extracción de datos de peticiones.
 * Encapsula lógica de manipulación de headers, cookies y datos de request.
 */
@Component
public class HttpRequestUtil {

    private static final String TOKEN_COOKIE_NAME = "X-AUTH-TOKEN";

    /**
     * Extrae el token JWT de la petición HTTP.
     * Busca primero en cookie, luego en Authorization header.
     *
     * @param request petición HTTP
     * @return token JWT si se encuentra, null en caso contrario
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        // Intenta obtener el token de la cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
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
     * Obtiene la dirección IP real del cliente considerando proxies.
     * Verifica headers X-Forwarded-For y X-Real-IP antes de usar remoteAddr.
     *
     * @param request petición HTTP
     * @return dirección IP del cliente
     */
    public String getClientIpAddress(HttpServletRequest request) {
        // Header X-Forwarded-For (usado por proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Tomar la primera IP si hay múltiples
            return xForwardedFor.split(",")[0].trim();
        }

        // Header X-Real-IP (usado por algunos proxies)
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // IP directa del cliente
        return request.getRemoteAddr();
    }

    /**
     * Obtiene el nombre de la cookie de token.
     *
     * @return nombre de la cookie
     */
    public static String getTokenCookieName() {
        return TOKEN_COOKIE_NAME;
    }
}
