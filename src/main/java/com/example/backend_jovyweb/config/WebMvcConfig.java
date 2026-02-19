package com.example.backend_jovyweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring Web MVC.
 * Registra interceptores y configura comportamientos de la web.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HttpLoggingInterceptor httpLoggingInterceptor;

    /**
     * Constructor que inyecta el interceptor HTTP.
     * 
     * @param httpLoggingInterceptor interceptor para loguear peticiones
     */
    public WebMvcConfig(HttpLoggingInterceptor httpLoggingInterceptor) {
        this.httpLoggingInterceptor = httpLoggingInterceptor;
    }

    /**
     * Registra el interceptor HTTP en todas las rutas.
     * 
     * @param registry registro de interceptores
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui**",
                        "/v3/api-docs**",
                        "/swagger-resources**",
                        "/webjars**");
    }
}
