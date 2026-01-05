package com.vinyl.VinylExchange.favorite;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vinyl.VinylExchange.favorite.dto.FavoriteRequest;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@Controller
@RequestMapping("/api/favorites/")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        Set<UUID> favorites = favoriteService.getUserFavorites(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favorites);
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody FavoriteRequest request) {

        favoriteService.addToFavorites(userPrincipal.getId(), request.listingId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<?> removeFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable(name = "listingId", required = true) UUID listingId) {

        favoriteService.removeFromFavorites(userPrincipal.getId(), listingId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
