package com.vinyl.VinylExchange.exception;

import java.util.List;

import com.vinyl.VinylExchange.domain.entity.CartValidationIssue;

public class CheckOutValidationException extends RuntimeException {
    public CheckOutValidationException(List<CartValidationIssue> issues) {
        super("Cart has one or more validation errors, cannot proceed: " + issues.toString());
    }
}
