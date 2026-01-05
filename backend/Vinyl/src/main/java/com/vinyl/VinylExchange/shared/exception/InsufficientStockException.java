package com.vinyl.VinylExchange.shared.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException() {
        super("Insufficient stock");
    }

    public InsufficientStockException(long requested, long available) {
        super("Requested " + requested + ", but only " + available + " available");
    }

    public InsufficientStockException(long available) {
        super("Only " + available + " item is in stock");
    }
}
