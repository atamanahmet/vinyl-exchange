package com.vinyl.VinylExchange.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("Cart Item Not Found");
    }

    public CartItemNotFoundException(String message) {
        super(message);
    }

}
