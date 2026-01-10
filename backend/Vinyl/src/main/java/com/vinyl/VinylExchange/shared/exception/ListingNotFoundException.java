package com.vinyl.VinylExchange.shared.exception;

public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException() {
        super("Listing is not exist or available anymore");
    }

    public ListingNotFoundException(String message) {
        super(message);
    }

}
