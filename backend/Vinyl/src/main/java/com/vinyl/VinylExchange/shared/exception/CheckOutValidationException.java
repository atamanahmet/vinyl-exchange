package com.vinyl.VinylExchange.shared.exception;

import java.util.List;

import com.vinyl.VinylExchange.cart.CartValidationIssue;

public class CheckOutValidationException extends RuntimeException {
    public CheckOutValidationException(List<CartValidationIssue> issues) {
        super("Cart has one or more validation errors, cannot proceed: " + issues.toString());
    }
}
