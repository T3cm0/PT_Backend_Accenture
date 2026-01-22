package com.sebastianrodriguez.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Arranca el contexto de Spring Boot.
	 *
	 * @param args argumentos de linea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
