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
import com.vinyl.VinylExchange.domain.dto.UpdateCartItemRequest;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

        private final CartService cartService;

        public CartController(CartService cartService) {

                this.cartService = cartService;
        }

        @GetMapping
        public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                CartDTO cartDTO = cartService.getCartDTO(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(cartDTO);
        }

        @PostMapping("/items")
        public ResponseEntity<?> addToCart(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestBody AddToCartRequestDTO requestDTO) {

                CartDTO cartDTO = cartService.addToCart(
                                userPrincipal.getId(), // UUID
                                requestDTO.listingId(), // UUID
                                requestDTO.quantity()); // int

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(cartDTO);
        }

        @DeleteMapping("/items/{cartItemId}")
        public ResponseEntity<?> removeFromCart(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "cartItemId", required = true) UUID cartItemId) {

                CartDTO cartDTO = cartService.removeItemFromCart(
                                userPrincipal.getId(),
                                cartItemId);

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .body(cartDTO);
        }

        @PatchMapping("/items/")
        public ResponseEntity<?> updateCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "cartItemId", required = true) UUID cartItemId,
                        @RequestBody UpdateCartItemRequest request) {

                cartService.updateCartItem(
                                userPrincipal.getId(),
                                cartItemId,
                                request);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .build();
        }

        @DeleteMapping
        public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                cartService.clearCart(userPrincipal.getId());

                return ResponseEntity
                                .noContent()
                                .build();
        }
}
