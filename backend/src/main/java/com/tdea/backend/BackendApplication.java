package com.tdea.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada para iniciar la API de inventario y flujo de caja. */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Arranca Spring Boot y los componentes REST del prototipo.
	 *
	 * @param args argumentos recibidos desde Maven o la línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
