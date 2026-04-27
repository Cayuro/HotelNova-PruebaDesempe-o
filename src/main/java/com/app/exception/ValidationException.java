package com.app.exception;

/**
 * Excepción lanzada cuando la validación de datos falla.
 * Hereda de BusinessException.
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
