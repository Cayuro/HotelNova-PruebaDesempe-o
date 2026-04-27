package com.app.exception;

/**
 * Lanzada cuando un recurso solicitado no existe.
 * Ejemplo: buscar habitación con ID que no existe.
 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
