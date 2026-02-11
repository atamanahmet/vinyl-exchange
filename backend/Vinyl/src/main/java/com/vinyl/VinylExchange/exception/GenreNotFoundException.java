package com.vinyl.VinylExchange.exception;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
        super("Genre Not Found");
    }
}
