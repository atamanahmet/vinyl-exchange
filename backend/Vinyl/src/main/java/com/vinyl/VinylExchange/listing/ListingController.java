package com.vinyl.VinylExchange.listing;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.vinyl.VinylExchange.listing.dto.FreezeRequest;
import com.vinyl.VinylExchange.listing.dto.ListingDTO;
import com.vinyl.VinylExchange.listing.dto.PricePreviewRequest;
import com.vinyl.VinylExchange.listing.dto.PromoteRequest;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.shared.FileStorageService;
import com.vinyl.VinylExchange.user.User;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

        private final ListingService listingService;
        private final PricePreviewService pricePreviewService;
        private final FileStorageService fileStorageService;

        public ListingController(
                        ListingService listingService,
                        PricePreviewService pricePreviewService,
                        FileStorageService fileStorageService) {

                this.listingService = listingService;
                this.pricePreviewService = pricePreviewService;
                this.fileStorageService = fileStorageService;
        }

        @GetMapping
        public ResponseEntity<?> getListings() {

                List<Listing> listings = listingService.getAvailableListings();

                List<ListingDTO> listingDTOs = listingService.getListingDTOs();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listingDTOs);
        }

        // admin
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/all")
        public ResponseEntity<?> getAllListings() {

                List<Listing> listings = listingService.getAllListings();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listings);
        }

        @GetMapping("/promote")
        public ResponseEntity<?> getPromotedListings(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                List<Listing> promoteListings = listingService.getFilteredPromotedListings(userPrincipal.getId());
                // System.out.println(promoteListings.get(0).getTitle());
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(promoteListings);
        }

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> createListing(
                        @AuthenticationPrincipal UserPrincipal currentUserPrincipal,
                        @RequestPart("listing") String listingJson,
                        @RequestPart(value = "images", required = false) List<MultipartFile> images) {

                User user = currentUserPrincipal.getUser();

                listingService.createNewListing(
                                listingJson,
                                images,
                                user);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body("Listing created");
        }

        @GetMapping("/my")
        public ResponseEntity<?> getMyListings(@AuthenticationPrincipal UserPrincipal currentUserPrincipal) {

                List<ListingDTO> listingResponse = listingService.getListingDTOsByUserId(currentUserPrincipal.getId());

                listingResponse = listingService.getListingDTOsByUserId(currentUserPrincipal.getId());

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
        public ResponseEntity<?> deleteListing(@AuthenticationPrincipal UserPrincipal currentUserPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId) {

                listingService.deleteListing(listingId, currentUserPrincipal);

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }

        @PostMapping("/price/preview")
        public BigDecimal previewPrice(
                        @AuthenticationPrincipal UserPrincipal currentUserPrincipal,
                        @RequestBody PricePreviewRequest pricePreviewRequestDTO) {

                if (pricePreviewRequestDTO.priceTL() != null) {
                        return pricePreviewService.previewDiscountedPrice(
                                        pricePreviewRequestDTO.priceTL(),
                                        pricePreviewRequestDTO.discountPercent());
                }
                return null;
        }

        // admin
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/promote/{listingId}")
        public ResponseEntity<?> promoteListing(
                        @AuthenticationPrincipal UserPrincipal currentUserPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId,
                        @RequestBody PromoteRequest promoteRequest) {

                System.out.println("Curret User: " + currentUserPrincipal.getUsername());

                System.out.println("Curret User ROles: " + currentUserPrincipal.getRoles());

                listingService.promoteListing(listingId, promoteRequest.action(), currentUserPrincipal);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .build();
        }

        // admin
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/freeze/{listingId}")
        public ResponseEntity<?> freezeListing(
                        @AuthenticationPrincipal UserPrincipal currentUserPrincipal,
                        @PathVariable(name = "listingId", required = true) UUID listingId,
                        @RequestBody FreezeRequest freezeRequest) {

                System.out.println("Curret User: " + currentUserPrincipal.getUsername());

                System.out.println("Curret User ROles: " + currentUserPrincipal.getRoles());

                listingService.freezeListing(listingId, freezeRequest.action(), currentUserPrincipal);

                if (freezeRequest.action()) {

                        listingService.promoteListing(listingId, false, currentUserPrincipal);
                }

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .build();
        }
}
