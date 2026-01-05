package com.vinyl.VinylExchange.cart;

import java.util.UUID;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartValidationIssue {
    private UUID cartItemId;
    private UUID listingId;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private ErrorType errorType;
    private String message;

    public enum IssueType {

        SOLD_OUT,
        LISTING_DELETED,
        INSUFFICIENT_STOCK,
        PRICE_CHANGED,
        SELLER_DEACTIVATED
    }

    public enum ErrorType {

        ERROR,
        WARNING
    }
}
