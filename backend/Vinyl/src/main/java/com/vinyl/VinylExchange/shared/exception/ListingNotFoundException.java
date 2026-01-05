package com.vinyl.VinylExchange.shared.exception;

public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException() {
        super("Listing not found");
    }

    public ListingNotFoundException(String message) {
        super(message);
    }

}
