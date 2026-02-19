package com.example.backend_jovyweb.config;

import com.example.backend_jovyweb.modules.auth.service.AuthService;
import com.example.backend_jovyweb.modules.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro JWT de Spring Security para validar tokens en cada petición.
 * Se ejecuta en el SecurityFilterChain ANTES del JwtInterceptor.
 * 
 * Valida el JWT y configura el SecurityContext para que Spring Security
 * reconozca al usuario como autenticado.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extraer el token de la cookie o del header Authorization
            String token = extractTokenFromRequest(request);

            // Si hay token, validar directamente con JwtUtil (sin dependencia de sesión en
            // memoria)
            if (token != null && jwtUtil.validateToken(token)) {
                // Obtener el código de empleado del token
                Integer employeeCode = jwtUtil.getEmployeeCodeFromToken(token);

                if (employeeCode != null) {
                    // Crear un token de autenticación sin credenciales específicas
                    // Usamos empleeeCode como nombre de usuario
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            employeeCode.toString(),
                            null,
                            new ArrayList<>()); // Sin roles por ahora

                    // Configurar el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Token validado para empleado: {}", employeeCode);
                }
            }

        } catch (Exception e) {
            logger.debug("Error al procesar JWT: {}", e.getMessage());
            // No hacer nada, dejar que Spring Security maneje la autenticación
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token de la cookie o del header Authorization.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Intenta obtener el token de la cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-AUTH-TOKEN".equals(cookie.getName())) {
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
}
