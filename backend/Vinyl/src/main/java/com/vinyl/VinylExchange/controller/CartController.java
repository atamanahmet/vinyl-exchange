package com.vinyl.VinylExchange.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.domain.dto.AddToCartRequestDTO;
import com.vinyl.VinylExchange.domain.dto.CartDTO;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {

        this.cartService = cartService;
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        CartDTO cartDTO = cartService.getCartDTO(userPrincipal.getUser());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartDTO);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody AddToCartRequestDTO requestDTO) {

        cartService.addToCart(
                userPrincipal.getUser(),
                requestDTO.listingId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable(name = "cartItemId", required = true) UUID cartItemId) {

        cartService.removeFromCart(
                userPrincipal.getUser(),
                cartItemId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<?> decreaseFromCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable(name = "cartItemId", required = true) UUID cartItemId) {

        cartService.decreaseItemQuantity(
                userPrincipal.getUser(),
                cartItemId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
