package com.vinyl.VinylExchange.shared.exception;

public class CheckOutProcessingException extends RuntimeException {
    public CheckOutProcessingException() {
        super("Checkout processing failed");
    }
}
