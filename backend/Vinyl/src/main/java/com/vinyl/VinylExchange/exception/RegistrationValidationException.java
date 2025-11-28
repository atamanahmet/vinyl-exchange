package com.vinyl.VinylExchange.exception;

public class RegistrationValidationException extends RuntimeException {

    // private final Map<String, String> errors;

    public RegistrationValidationException() {
        super("Registration validation failed");
    }

    public RegistrationValidationException(String message) {
        super(message);
    }

    // public Map<String, String> getErrors() {
    // return this.errors;
    // }
}
