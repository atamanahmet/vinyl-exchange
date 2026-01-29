package com.vinyl.VinylExchange.whislist;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.whislist.dto.AddToWishlistRequest;
import com.vinyl.VinylExchange.whislist.dto.WishlistItemDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getMyWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<WishlistItemDTO> wishlistDTO = wishlistService.getWishlistByUserId(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(wishlistDTO);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> addToWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody AddToWishlistRequest request) {

        System.out.println(request.getTitle() + request.getArtist() + request.getYear() + request.getImageUrl());

        wishlistService.addToWishlist(userPrincipal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    @DeleteMapping("/{wishlistItemId}")
    public ResponseEntity<HttpStatus> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable(name = "wishlistItemId") Long wishlistItemId) {

        wishlistService.removeFromWishlist(userPrincipal.getId(), wishlistItemId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
