package com.app.exception;

/**
 * Lanzada cuando el usuario no tiene permisos para realizar una acción.
 * Ejemplo: RECEPCIONISTA intentando acceder a funciones de ADMIN.
 */
public class AuthorizationException extends BusinessException {
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
