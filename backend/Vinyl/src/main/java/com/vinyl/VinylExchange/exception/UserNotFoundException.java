package com.vinyl.VinylExchange.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User Not Found");

    }
    public UserNotFoundException(String message) {
        super(message);

    }
}
