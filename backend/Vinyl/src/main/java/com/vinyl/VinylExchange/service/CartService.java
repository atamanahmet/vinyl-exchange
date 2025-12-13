package com.vinyl.VinylExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.dto.CartDTO;
import com.vinyl.VinylExchange.domain.dto.CartItemDTO;
import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.CartItem;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.CartItemNotFoundException;
import com.vinyl.VinylExchange.exception.InsufficientStockException;
import com.vinyl.VinylExchange.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ListingService listingService;

    public CartService(CartRepository cartRepository, ListingService listingService) {

        this.cartRepository = cartRepository;
        this.listingService = listingService;
    }

    public CartDTO getCartDTO(User user) {

        Cart cart = cartRepository
                .findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
        return new CartDTO(cart);
    }

    public Cart getOrCreateCart(User user) {

        return cartRepository
                .findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);
                    user.setCart(newCart);

                    return cartRepository.save(newCart);
                });
    }

    public Cart addToCart(User user, UUID listingId) {

        Listing listing = listingService.getListingById(listingId);

        if (listing.getQuantity() < 1) {
            throw new InsufficientStockException();
        }

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getListing().getId().equals(listingId)).findFirst()
                .orElse(null);

        if (cartItem == null) {

            cartItem = new CartItem(cart, listing);
            cart.getItems().add(cartItem);

        } else {

            if (cartItem.getQuantity() + 1 > listing.getQuantity()) {
                throw new InsufficientStockException();
            }

            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }

        return cartRepository.save(cart);
    }

    public void removeFromCart(User user, UUID cartItemId) {

        Cart cart = getOrCreateCart(user);

        if (cart.getItems().isEmpty()) {
            throw new CartItemNotFoundException("Cart is empty");
        }

        CartItem cartItem = cart
                .getItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException());

        cart.getItems().remove(cartItem);

        cartRepository.save(cart);
    }

    public void decreaseItemQuantity(User user, UUID cartItemId) {

        Cart cart = getOrCreateCart(user);

        if (cart.getItems().isEmpty()) {
            throw new CartItemNotFoundException("Cart is already empty");
        }

        CartItem cartItem = cart
                .getItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException());

        if (cartItem.getQuantity() == 1) {

            cart.getItems().remove(cartItem);
        } else {

            cartItem.setQuantity(cartItem.getQuantity() - 1);
        }

        cartRepository.save(cart);
    }

    public List<CartItemDTO> getCartItems(User user) {

        Cart cart = getOrCreateCart(user);

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        cart.getItems()
                .forEach(item -> cartItemDTOs.add(new CartItemDTO(item)));

        return cartItemDTOs;
    }
}
