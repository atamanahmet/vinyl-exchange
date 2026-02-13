package com.vinyl.VinylExchange.exception;

public class ListingCreationException extends RuntimeException {
    public ListingCreationException(String message, String username) {
        super(message);
    }
}
