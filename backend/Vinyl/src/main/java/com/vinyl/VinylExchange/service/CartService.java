package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {

        this.cartRepository = cartRepository;
    }

    public Cart getOrCreateCart(User user) {

        return cartRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }

    public Cart addToCart(User user, UUID listingId) {

        Cart userCart = getOrCreateCart(user);

        userCart.getListingIds().add(listingId);

        return cartRepository.save(userCart);
    }

    public Cart removeFromCart(User user, UUID listingId) {

        Cart userCart = getOrCreateCart(user);

        userCart.getListingIds().remove(listingId);

        return cartRepository.save(userCart);
    }

    public List<UUID> getListingIds(User user) {

        Cart userCart = getOrCreateCart(user);

        return userCart.getListingIds();
    }
}
