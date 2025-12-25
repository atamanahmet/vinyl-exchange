package com.vinyl.VinylExchange.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("Token Expired. Revoking token..");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
