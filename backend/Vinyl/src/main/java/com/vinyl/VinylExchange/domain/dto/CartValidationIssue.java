package com.vinyl.VinylExchange.domain.dto;

import java.util.UUID;

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
    private IssueType type;
    private String message;

    public enum IssueType {
        SOLD_OUT,
        LISTING_DELETED,
        INSUFFICIENT_STOCK,
        PRICE_CHANGED,
        SELLER_DEACTIVATED
    }
}
