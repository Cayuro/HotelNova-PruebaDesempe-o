package com.app.exception;

/**
 * Lanzada cuando la autenticación falla.
 * Ejemplo: usuario/password inválido o usuario inactivo.
 */
public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
