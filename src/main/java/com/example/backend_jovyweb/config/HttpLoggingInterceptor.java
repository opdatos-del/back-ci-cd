package com.example.backend_jovyweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Interceptor HTTP para loguear todas las peticiones y respuestas.
 * Útil para debugging y monitoreo de la API en tiempo real.
 */
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingInterceptor.class);

    /**
     * Se ejecuta antes de que se procese la petición.
     * Loguea información de la solicitud entrante.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null) {
            requestId = String.valueOf(System.currentTimeMillis());
        }

        request.setAttribute("requestId", requestId);
        request.setAttribute("startTime", System.currentTimeMillis());

        logger.info("=".repeat(80));
        logger.info("📥 PETICIÓN ENTRANTE [{}]", requestId);
        logger.info("Método: {} | URI: {}", request.getMethod(), request.getRequestURI());
        logger.info("Remote Address: {} | User Agent: {}",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        // Loguear headers importantes
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames.hasMoreElements()) {
            logger.debug("Headers:");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (!headerName.equalsIgnoreCase("Authorization")) {
                    logger.debug("  {}: {}", headerName, headerValue);
                }
            }
        }

        // Loguear parámetros
        if (request.getQueryString() != null) {
            logger.debug("Query Parameters: {}", request.getQueryString());
        }

        return true;
    }

    /**
     * Se ejecuta después de que se procese la petición.
     * Loguea información de la respuesta.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

        String requestId = (String) request.getAttribute("requestId");
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        logger.info("📤 RESPUESTA [{}]", requestId);
        logger.info("Status: {} | Duration: {} ms", response.getStatus(), duration);
        logger.info("Content-Type: {}", response.getContentType());

        if (ex != null) {
            logger.error("❌ ERROR en la petición [{}]: {}", requestId, ex.getMessage(), ex);
        } else if (response.getStatus() >= 400) {
            logger.warn("⚠️  Respuesta con error [{}]", requestId);
        } else {
            logger.info("✅ Petición completada exitosamente");
        }

        logger.info("=".repeat(80));
    }
}
