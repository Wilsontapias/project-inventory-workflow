package com.tdea.backend.common.exception;

/**
 * Excepción de dominio para recursos que no existen en el prototipo.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje apto para una respuesta REST.
     *
     * @param message descripción del recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
