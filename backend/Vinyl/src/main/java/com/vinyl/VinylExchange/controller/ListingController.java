package com.vinyl.VinylExchange.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vinyl.VinylExchange.domain.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.service.ListingService;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ListingController {
        private final ListingService listingService;

        public ListingController(
                        ListingService listingService) {

                this.listingService = listingService;
        }

        @PostMapping(value = "/newlisting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> createVinylListing(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestPart("listing") String listingJson,
                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {

                User user = userPrincipal.getUser();

                listingService.createNewListing(
                                listingJson,
                                images,
                                user);

                return ResponseEntity.status(HttpStatus.OK)
                                .body("Listing created");

        }

        // @PostMapping(value = "/listing/{listingId}/edit", consumes =
        // MediaType.MULTIPART_FORM_DATA_VALUE)
        // public ResponseEntity<?> editListing(
        // @AuthenticationPrincipal UserPrincipal userPrincipal,
        // @RequestPart("listing") String listingJson,
        // @RequestPart(value = "images", required = false) List<MultipartFile> images,
        // @PathVariable(name = "listingId", required = true) UUID listingId,
        // HttpServletRequest request,
        // HttpServletResponse response) throws IOException {

        // listingService.saveEditedListing(
        // listingId,
        // listingJson,
        // images);

        // return ResponseEntity.status(HttpStatus.OK)
        // .body("Listing created");

        // }

        @GetMapping("/mylistings")
        public ResponseEntity<?> getMyListings(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                List<ListingDTO> listingResponse = listingService.getListingDTOsByUserId(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listingResponse);
        }

        @GetMapping("/listing/{listingId}")
        public ResponseEntity<?> getListing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId) {

                // ListingDTO listingDTO = listingService.getListingDTOById(listingId);
                Listing listing = listingService.getListingById(listingId);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listing);

        }

        @DeleteMapping("/delete/{listingId}")
        public ResponseEntity<?> deleteListing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId) {

                listingService.deleteListing(listingId, userPrincipal);

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }

}
