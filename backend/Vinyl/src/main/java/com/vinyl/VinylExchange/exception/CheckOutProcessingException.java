package com.vinyl.VinylExchange.exception;

public class CheckOutProcessingException extends RuntimeException {
    public CheckOutProcessingException() {
        super("Checkout processing failed");
    }
}
