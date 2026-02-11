package com.vinyl.VinylExchange.exception;

public class InvalidOrderOperationException extends RuntimeException {
    public InvalidOrderOperationException() {
        super("Order not found");
    }

    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
