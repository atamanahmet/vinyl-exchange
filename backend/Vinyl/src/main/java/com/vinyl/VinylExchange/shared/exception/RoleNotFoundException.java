package com.vinyl.VinylExchange.shared.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("Role not found in db.");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
