package com.vinyl.VinylExchange.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException() {
        super("OrderItem not found");
    }

    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
