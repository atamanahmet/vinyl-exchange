package com.vinyl.VinylExchange.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.vinyl.VinylExchange.domain.dto.CartDTO;
import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.service.CartService;
import com.vinyl.VinylExchange.service.ListingService;

@Controller
public class CartController {

    private final ListingService listingService;
    private final CartService cartService;

    public CartController(ListingService listingService, CartService cartService) {

        this.listingService = listingService;
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        Cart cart = cartService.getOrCreateCart(userPrincipal.getUser());

        List<Listing> cartListings = listingService.getListingsByIds(cart.getListingIds());

        CartDTO cartDTO = new CartDTO(cartListings);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartDTO);
    }
}
