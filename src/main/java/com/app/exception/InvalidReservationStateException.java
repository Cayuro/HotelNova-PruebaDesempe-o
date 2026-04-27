package com.app.exception;

/**
 * Lanzada cuando se intenta hacer una operación sobre una reserva en estado inválido.
 * Ejemplo: check-out en reserva BOOKED.
 */
public class InvalidReservationStateException extends BusinessException {
    public InvalidReservationStateException(String message) {
        super(message);
    }

    public InvalidReservationStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
