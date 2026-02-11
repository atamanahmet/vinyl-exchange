package com.vinyl.VinylExchange.exception;

public class RegistrationValidationException extends RuntimeException {

    public RegistrationValidationException() {
        super("Registration validation failed");
    }

    public RegistrationValidationException(String message) {
        super(message);
    }

}
