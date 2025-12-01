package com.vinyl.VinylExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.TokenExpireException;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.service.AuthService;
import com.vinyl.VinylExchange.service.ListingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ListingController {
    private final ListingService listingService;
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    public ListingController(AuthService authService,
            JwtCookieUtil jwtCookieUtil, ListingService listingService) {
        this.authService = authService;
        this.jwtCookieUtil = jwtCookieUtil;
        this.listingService = listingService;
    }

    @PostMapping("/newlisting")
    public ResponseEntity<?> createVinylListing(@RequestBody Listing listing, HttpServletRequest request,
            HttpServletResponse response) {

        try {

            String token = jwtCookieUtil.getTokenFromRequest(request);

            User user = authService.getUserFromToken(token);

            listing.setOwner(user); // logged in user from jwt

            listingService.saveListing(listing);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Succesfully created");

        } catch (TokenExpireException e) {

            jwtCookieUtil.revokeJwtCookie(response);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

    }
}
