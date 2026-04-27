package com.app.exception;

/**
 * Lanzada cuando se intenta crear una entidad con un campo único que ya existe.
 * Caso genérico para cualquier duplicado.
 */
public class DuplicateEntityException extends BusinessException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
