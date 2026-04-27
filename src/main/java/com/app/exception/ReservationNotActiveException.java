package com.app.exception;

public class ReservationNotActiveException extends BusinessException {
    public ReservationNotActiveException(String message) {
        super(message);
    }
}
