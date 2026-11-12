package com.tdea.backend.common.exception;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Traduce errores de aplicación y validación a respuestas REST consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Convierte un recurso inexistente en HTTP 404.
     *
     * @param exception error de dominio
     * @return detalle Problem Details con el estado 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Recurso no encontrado", exception.getMessage());
    }

    /**
     * Convierte una violación de unicidad o relación en HTTP 409.
     *
     * @param exception error de dominio
     * @return detalle Problem Details con el estado 409
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException exception) {
        return problem(HttpStatus.CONFLICT, "Conflicto de negocio", exception.getMessage());
    }

    /**
     * Devuelve los errores de validación asociados a los campos del DTO.
     *
     * @param exception errores detectados por Jakarta Bean Validation
     * @return detalle HTTP 400 con un mapa de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidBody(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Uno o más campos tienen valores inválidos.");
        detail.setTitle("Solicitud inválida");
        detail.setType(URI.create("https://example.invalid/problems/validation"));
        detail.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(detail);
    }

    /**
     * Convierte errores de validación de parámetros de ruta o consulta en HTTP 400.
     *
     * @param exception error de validación de método
     * @return detalle Problem Details con el estado 400
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleInvalidParameter(HandlerMethodValidationException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Parámetro inválido", "Revise los parámetros enviados a la API.");
    }

    /**
     * Convierte enums, fechas o tipos de parámetros inválidos en HTTP 400.
     *
     * @param exception error de conversión de un parámetro
     * @return detalle Problem Details con el estado 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Parámetro inválido",
                "El parámetro '" + exception.getName() + "' tiene un valor o formato no válido.");
    }

    /**
     * Convierte cuerpos JSON malformados en HTTP 400.
     *
     * @param exception error de lectura del cuerpo
     * @return detalle Problem Details con el estado 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Cuerpo de solicitud inválido",
                "El JSON enviado no tiene el formato esperado.");
    }

    /**
     * Convierte valores de filtros no reconocidos en HTTP 400.
     *
     * @param exception error de conversión o filtro
     * @return detalle Problem Details con el estado 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Solicitud inválida", exception.getMessage());
    }

    /**
     * Evita enviar detalles internos de errores inesperados al cliente.
     *
     * @param exception error no controlado
     * @return detalle genérico HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception exception) {
        LOGGER.error("Error no controlado al atender la solicitud REST", exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado. Consulte los registros del backend.");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String message) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(title);
        detail.setType(URI.create("https://example.invalid/problems/" + status.value()));
        return ResponseEntity.status(status).body(detail);
    }
}
