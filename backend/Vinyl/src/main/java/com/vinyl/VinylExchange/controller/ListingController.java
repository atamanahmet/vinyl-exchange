package com.vinyl.VinylExchange.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vinyl.VinylExchange.domain.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.dto.PricePreviewRequestDTO;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.service.ListingService;
import com.vinyl.VinylExchange.service.PricePreviewService;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
        private final ListingService listingService;
        private final PricePreviewService pricePreviewService;

        public ListingController(
                        ListingService listingService,
                        PricePreviewService pricePreviewService) {

                this.listingService = listingService;
                this.pricePreviewService = pricePreviewService;
        }

        @GetMapping
        public ResponseEntity<?> getListings() {

                List<Listing> listings = listingService.getListings();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listings);
        }

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> createListing(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestPart("listing") String listingJson,
                        @RequestPart(value = "images", required = false) List<MultipartFile> images) {

                User user = userPrincipal.getUser();

                listingService.createNewListing(
                                listingJson,
                                images,
                                user);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body("Listing created");

        }

        @GetMapping("/my")
        public ResponseEntity<?> getMyListings(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                List<ListingDTO> listingResponse = listingService.getListingDTOsByUserId(userPrincipal.getId());

                listingResponse = listingService.getListingDTOsByUserId(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listingResponse);
        }

        @GetMapping("/{listingId}")
        public ResponseEntity<?> getListing(@PathVariable(name = "listingId", required = true) UUID listingId) {

                ListingDTO listingDTO = listingService.getListingDTOById(listingId);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listingDTO);

        }

        @DeleteMapping("/{listingId}")
        public ResponseEntity<?> deleteListing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId) {

                listingService.deleteListing(listingId, userPrincipal);

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }

        @PostMapping("/price/preview")
        public BigDecimal previewPrice(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestBody PricePreviewRequestDTO pricePreviewRequestDTO) {

                return pricePreviewService.previewDiscountedPrice(
                                pricePreviewRequestDTO.priceTL(),
                                pricePreviewRequestDTO.discountPercent());
        }
}
