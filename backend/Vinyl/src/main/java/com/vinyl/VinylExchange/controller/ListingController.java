package com.vinyl.VinylExchange.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.TokenExpireException;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.service.AuthService;
import com.vinyl.VinylExchange.service.FileStorageService;
import com.vinyl.VinylExchange.service.ListingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ListingController {
    private final ListingService listingService;
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final FileStorageService fileStorageService;

    public ListingController(
            AuthService authService,
            JwtCookieUtil jwtCookieUtil,
            ListingService listingService,
            FileStorageService fileStorageService) {

        this.authService = authService;
        this.jwtCookieUtil = jwtCookieUtil;
        this.listingService = listingService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/newlisting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVinylListing(
            @RequestPart("listing") String listingJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        try {

            String token = jwtCookieUtil.extractTokenFromRequest(request);

            User user = authService.getUserFromToken(token);

            Listing listing = new ObjectMapper().readValue(listingJson, Listing.class);

            listing.setOwner(user);

            Listing savedListing = listingService.saveListing(listing);

            List<String> filePaths = fileStorageService.saveImages(images, savedListing.getId());

            savedListing.setImagePaths(filePaths);

            listingService.saveListing(savedListing);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Listing created");

        } catch (TokenExpireException e) {

            jwtCookieUtil.revokeJwtCookie(response);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

    }
}
