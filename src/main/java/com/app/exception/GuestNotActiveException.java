package com.app.exception;

public class GuestNotActiveException extends BusinessException {
    public GuestNotActiveException(String message) {
        super(message);
    }
}
