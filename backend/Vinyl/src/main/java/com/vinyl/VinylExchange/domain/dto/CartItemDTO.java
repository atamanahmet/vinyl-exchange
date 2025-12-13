package com.vinyl.VinylExchange.domain.dto;

import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.CartItem;

public record CartItemDTO(
        UUID itemId,
        UUID listingId,
        long priceKurus,
        int discountBp,
        int quantity

) {
    public CartItemDTO(CartItem cartItem) {
        this(
                cartItem.getId(),
                cartItem.getListing().getId(),
                cartItem.getPriceKurus(),
                cartItem.getDiscountBP(),
                cartItem.getQuantity());
    }
}
