package com.vinyl.VinylExchange.shared.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException() {
        super("Cms page not found");
    }
}
