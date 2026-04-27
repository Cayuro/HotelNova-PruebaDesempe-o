package com.app.exception;

public class TransactionException extends BusinessException {
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
