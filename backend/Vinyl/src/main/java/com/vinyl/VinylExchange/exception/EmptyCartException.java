package com.vinyl.VinylExchange.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Cart is Empty");
    }

}
