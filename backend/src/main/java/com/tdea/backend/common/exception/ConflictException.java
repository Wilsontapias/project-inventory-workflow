package com.tdea.backend.common.exception;

/**
 * Excepción de dominio para operaciones que incumplen una regla de unicidad o relación.
 */
public class ConflictException extends RuntimeException {

    /**
     * Crea la excepción con una explicación del conflicto.
     *
     * @param message descripción de la regla que impide la operación
     */
    public ConflictException(String message) {
        super(message);
    }
}
