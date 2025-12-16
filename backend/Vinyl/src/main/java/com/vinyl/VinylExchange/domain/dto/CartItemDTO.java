package com.vinyl.VinylExchange.domain.dto;

import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.CartItem;

public record CartItemDTO(
        UUID id,
        UUID listingId,
        int quantity,
        String mainImagePath

) {
    public CartItemDTO(CartItem cartItem) {
        this(
                cartItem.getId(),
                cartItem.getListing().getId(),
                cartItem.getQuantity(),
                cartItem.getMainImagePath());
    }

}
