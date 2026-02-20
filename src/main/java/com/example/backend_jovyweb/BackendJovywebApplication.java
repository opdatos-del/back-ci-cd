package com.example.backend_jovyweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Clase principal de la aplicación Spring Boot.
 * Configura el escaneo de componentes para cargar los controladores, servicios
 * y repositorios.
 */
@SpringBootApplication
@PropertySource("file:.env")
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
