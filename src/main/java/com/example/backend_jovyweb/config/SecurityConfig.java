package com.example.backend_jovyweb.config;

import com.example.backend_jovyweb.modules.auth.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Configuración de seguridad de Spring Boot.
 * Define políticas de CORS, CSRF, autenticación y autorización.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

        @Autowired
        private JwtInterceptor jwtInterceptor;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        /**
         * Configura el SecurityFilterChain.
         * Establece políticas de autenticación, CORS y CSRF.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Agregar filtro JWT ANTES de los filtros de autenticación estándar
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                                // Deshabilitar CSRF ya que usamos JWT y cookies HttpOnly
                                .csrf(csrf -> csrf.disable())

                                // Configurar CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Configurar gestión de sesiones (stateless con JWT)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Configurar autorización de rutas
                                .authorizeHttpRequests(authz -> authz
                                                // Rutas públicas (endpoints de autenticación)
                                                .requestMatchers("/api/auth/login").permitAll()
                                                .requestMatchers("/api/auth/logout").permitAll()
                                                .requestMatchers("/api/auth/validate").permitAll()
                                                .requestMatchers("/api/auth/refresh").permitAll() // Refresh token
                                                                                                  // endpoint
                                                .requestMatchers("/api/auth/debug/**").permitAll() // Debug endpoint
                                                // Rutas públicas (documentación y consolas)
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // Todas las demás rutas requieren autenticación
                                                .anyRequest().authenticated())

                                // Configurar headers de seguridad
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives("default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline'; " +
                                                                                "style-src 'self' 'unsafe-inline'; " +
                                                                                "img-src 'self' data:;"))
                                                .frameOptions(frame -> frame.disable()) // Permitir h2-console en frames
                                );

                return http.build();
        }

        /**
         * Configura CORS.
         * Permite peticiones desde diferentes orígenes con ciertos headers.
         * 
         * IMPORTANTE: Cambiar allowedOrigins en producción a los dominios reales.
         * No dejar localhost en producción.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Orígenes permitidos - CAMBIAR PARA PRODUCCIÓN
                // Usar variables de entorno para configurar dinámicamente
                String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
                if (allowedOrigins != null && !allowedOrigins.isBlank()) {
                        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                } else {
                        // Fallback para desarrollo
                        configuration.setAllowedOrigins(Arrays.asList(
                                        "http://localhost:3000",
                                        "http://localhost:4200",
                                        "http://localhost:8080"));
                }

                // Métodos HTTP permitidos
                configuration.setAllowedMethods(Arrays.asList(
                                HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.PUT.name(),
                                HttpMethod.DELETE.name(),
                                HttpMethod.OPTIONS.name(),
                                HttpMethod.PATCH.name()));

                // Headers permitidos
                configuration.setAllowedHeaders(Arrays.asList(
                                "Content-Type",
                                "Authorization",
                                "X-CSRF-Token",
                                "X-Requested-With",
                                "X-AUTH-TOKEN"));

                // Headers expuestos en la respuesta
                configuration.setExposedHeaders(Arrays.asList(
                                "Content-Type",
                                "Authorization",
                                "X-CSRF-Token",
                                "X-Access-Token",
                                "X-Refresh-Token"));

                // Permitir credenciales (cookies)
                configuration.setAllowCredentials(true);

                // Tiempo de caché de preflight requests (menos en producción)
                configuration.setMaxAge(3600L); // 1 hora

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * Registra el interceptor JWT.
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jwtInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns(
                                                "/api/auth/login",
                                                "/api/auth/validate",
                                                "/api/auth/logout",
                                                "/api/auth/refresh"); // Refresh usa refreshToken, no JWT
        }

        /**
         * Cifrador de contraseñas.
         * Aunque no usamos contraseñas (usamos .ps1), lo dejamos disponible.
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
