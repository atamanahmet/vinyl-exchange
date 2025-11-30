package com.vinyl.VinylExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.service.AuthService;
import com.vinyl.VinylExchange.service.ListingService;
import com.vinyl.VinylExchange.service.UserService;
import com.vinyl.VinylExchange.service.VinylService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class VinylController {
    private final VinylService vinylService;
    private final ListingService listingService;
    private final UserService userService;
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    public VinylController(VinylService vinylService, UserService userService, AuthService authService,
            JwtCookieUtil jwtCookieUtil, ListingService listingService) {
        this.vinylService = vinylService;
        this.userService = userService;
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

        } catch (TokenExpiredException e) {

            jwtCookieUtil.revokeJwtCookie(response);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

    }
}
