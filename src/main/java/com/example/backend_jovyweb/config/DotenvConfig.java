package com.example.backend_jovyweb.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para cargar variables de entorno desde el archivo .env.
 * 
 * <p>
 * Este listener se ejecuta INMEDIATAMENTE después de que el Environment se
 * prepara, pero ANTES de que se cree la fábrica de beans. Esto garantiza que
 * todas las variables de .env estén disponibles cuando Spring intente resolver
 * los placeholders en application.properties.
 * </p>
 * 
 * El archivo .env debe estar ubicado en la raíz del proyecto. Si el archivo
 * no existe, la configuración lo ignora gracefully sin lanzar excepciones.
 * 
 * @see io.github.cdimascio.dotenv.Dotenv
 */
public class DotenvConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String DOTENV_SOURCE = "dotenv";

    /**
     * Se ejecuta cuando el ambiente de Spring está siendo preparado.
     * Este es el momento más temprano para cargar las variables externas.
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        // Cargar el archivo .env
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Convertir las variables del .env a un mapa de propiedades
        Map<String, Object> envProperties = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            envProperties.put(entry.getKey(), entry.getValue());
            // También set en System.setProperty para compatibilidad
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // Agregar como MapPropertySource al ambiente de Spring con máxima prioridad
        if (!envProperties.isEmpty()) {
            environment.getPropertySources()
                    .addFirst(new MapPropertySource(DOTENV_SOURCE, envProperties));
        }
    }
}
