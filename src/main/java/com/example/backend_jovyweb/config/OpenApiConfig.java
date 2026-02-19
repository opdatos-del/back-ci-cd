package com.example.backend_jovyweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

/**
 * Configuración de OpenAPI (Swagger) para la documentación automática de la API
 * REST.
 * Define información general sobre la API que se mostrará en Swagger UI.
 * Incluye configuración de seguridad JWT para que Swagger UI incluya el token
 * automáticamente en las peticiones.
 */
@Configuration
public class OpenApiConfig {

        /**
         * Crea la configuración personalizada de OpenAPI.
         * Define la información de la API y el esquema de seguridad JWT.
         * 
         * @return Objeto OpenAPI con la información de la API y seguridad configurada
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("backend-jovyweb API")
                                                .version("v0.1.0")
                                                .description("Documentación automática OpenAPI generada por springdoc-openapi"))

                                // Agregar esquema de seguridad JWT (en Cookie)
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("JWT token en header Authorization: Bearer <token>")));
        }

}
