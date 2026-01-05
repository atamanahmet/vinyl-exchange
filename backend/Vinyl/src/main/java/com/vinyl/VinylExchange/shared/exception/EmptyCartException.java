package com.vinyl.VinylExchange.shared.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Cart is Empty");
    }

}
