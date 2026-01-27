package com.vinyl.VinylExchange.shared.exception;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
        super("Genre Not Found");
    }
}
