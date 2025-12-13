package com.vinyl.VinylExchange.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException() {
        super("Insufficient stock available");
    }

    public InsufficientStockException(long requested, long available) {
        super("Requested " + requested + ", but only " + available + " available");
    }
}
