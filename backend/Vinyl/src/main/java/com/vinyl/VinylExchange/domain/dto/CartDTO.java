package com.vinyl.VinylExchange.domain.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vinyl.VinylExchange.domain.entity.Cart;

public record CartDTO(UUID cartId, List<CartItemDTO> cartItems) {

    public CartDTO(Cart cart) {
        this(
                cart.getId(),

                cart.getItems()
                        .stream()
                        .map(cartItem -> new CartItemDTO(cartItem)).collect(Collectors.toList()));
    }
}
