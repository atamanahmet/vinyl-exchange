package com.vinyl.VinylExchange.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("Role not found in db.");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
