package com.vinyl.VinylExchange.exception;

public class NoCurrentUserException extends RuntimeException {

    public NoCurrentUserException() {
        super("No current user present. Please Login");
    }

}
