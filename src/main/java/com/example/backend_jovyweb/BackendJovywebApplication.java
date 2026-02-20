package com.example.backend_jovyweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Clase principal de la aplicación Spring Boot.
 * Configura el escaneo de componentes para cargar los controladores, servicios
 * y repositorios.
 */
@SpringBootApplication
public class BackendJovywebApplication {

	/**
	 * Punto de entrada de la aplicación.
	 * 
	 * @param args Argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendJovywebApplication.class, args);
	}

}
