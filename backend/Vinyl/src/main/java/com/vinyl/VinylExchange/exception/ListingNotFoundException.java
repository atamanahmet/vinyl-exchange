package com.vinyl.VinylExchange.exception;

public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException() {
        super("Listing not found");
    }

}
