package com.vinyl.VinylExchange.domain.dto;

import java.util.List;

import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.CartItem;

public record CartDTO(List<CartItem> cartItems) {

    public CartDTO(Cart cart) {
        this(cart.getItems());
    }
}
