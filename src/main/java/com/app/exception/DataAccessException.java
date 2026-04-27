package com.app.exception;

/**
 * Wrapper de SQLException.
 * Traduce errores de infraestructura JDBC a excepciones de aplicación.
 */
public class DataAccessException extends BusinessException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
