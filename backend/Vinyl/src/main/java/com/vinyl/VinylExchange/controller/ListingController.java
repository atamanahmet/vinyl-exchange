package com.vinyl.VinylExchange.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vinyl.VinylExchange.domain.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.dto.PricePreviewRequestDTO;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.User;
// import com.vinyl.VinylExchange.service.CartService;
import com.vinyl.VinylExchange.service.ListingService;
import com.vinyl.VinylExchange.service.PricePreviewService;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ListingController {
        private final ListingService listingService;
        private final PricePreviewService pricePreviewService;
        // private final CartService cartService;

        public ListingController(
                        ListingService listingService,
                        PricePreviewService pricePreviewService
        // ,CartService cartService
        ) {

                this.listingService = listingService;
                this.pricePreviewService = pricePreviewService;
                // this.cartService = cartService;
        }

        @GetMapping("/")
        public ResponseEntity<?> getListings() {

                List<Listing> listings = listingService.getListings();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listings);
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

        @GetMapping("/mylistings")
        public ResponseEntity<?> getMyListings(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                List<ListingDTO> listingResponse = listingService.getListingDTOsByUserId(userPrincipal.getId());

                // if (listingResponse.isEmpty()) {
                // mockListings(userPrincipal.getUser());
                // }

                listingResponse = listingService.getListingDTOsByUserId(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(listingResponse);
        }

        @GetMapping("/listing/{listingId}")
        public ResponseEntity<?> getListing(@PathVariable(name = "listingId", required = true) UUID listingId) {

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

        @PostMapping("/price/preview")
        public BigDecimal previewPrice(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestBody PricePreviewRequestDTO pricePreviewRequestDTO,
                        HttpServletRequest request) throws IOException {

                System.out.println(pricePreviewRequestDTO.toString());

                return pricePreviewService.previewDiscountedPrice(
                                pricePreviewRequestDTO.priceTL(),
                                pricePreviewRequestDTO.discountPercent());
        }

        // private void mockListings(User user) {
        // List<Listing> listings = List.of(
        // new Listing(
        // UUID.randomUUID(),
        // "Dark Side of the Moon",
        // ListingStatus.AVAILABLE,
        // "1973",
        // "UK",
        // "5099902987613",
        // "12\"",
        // "Vinyl, LP",
        // "Classic progressive rock album.",
        // "Pink Floyd",
        // "PF001",
        // "Harvest",
        // "VG+",
        // 10,
        // true,
        // 29999L,
        // 28000L,
        // 500,
        // 5,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("pf_dark_side_1.jpg", "pf_dark_side_2.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Abbey Road",
        // ListingStatus.AVAILABLE,
        // "1969",
        // "UK",
        // "094638246817",
        // "12\"",
        // "Vinyl, LP",
        // "The Beatles’ iconic album.",
        // "The Beatles",
        // "TB001",
        // "Apple Records",
        // "NM",
        // 17,
        // true,
        // 34999L,
        // 33000L,
        // 0,
        // 3,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("abbey_road.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Thriller",
        // ListingStatus.AVAILABLE,
        // "1982",
        // "US",
        // "07464381122",
        // "12\"",
        // "Vinyl, LP",
        // "Best-selling album of all time.",
        // "Michael Jackson",
        // "MJ001",
        // "Epic",
        // "VG+",
        // 9,
        // false,
        // 27999L,
        // 25000L,
        // 1000,
        // 7,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("thriller.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Back in Black",
        // ListingStatus.AVAILABLE,
        // "1980",
        // "US",
        // "69699802071",
        // "12\"",
        // "Vinyl, LP",
        // "Hard rock classic.",
        // "AC/DC",
        // "ACDC001",
        // "Atlantic",
        // "VG",
        // 10,
        // true,
        // 25999L,
        // 24000L,
        // 0,
        // 4,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("back_in_black.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Nevermind",
        // ListingStatus.AVAILABLE,
        // "1991",
        // "US",
        // "720642442517",
        // "12\"",
        // "Vinyl, LP",
        // "Grunge-defining album.",
        // "Nirvana",
        // "NIR001",
        // "DGC",
        // "NM",
        // 12,
        // true,
        // 28999L,
        // 27000L,
        // 750,
        // 6,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("nevermind.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "The Wall",
        // ListingStatus.AVAILABLE,
        // "1979",
        // "UK",
        // "5099902987910",
        // "12\"",
        // "Vinyl, 2LP",
        // "Rock opera masterpiece.",
        // "Pink Floyd",
        // "PF002",
        // "Harvest",
        // "VG+",
        // 26,
        // true,
        // 39999L,
        // 37000L,
        // 500,
        // 2,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("the_wall.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Kind of Blue",
        // ListingStatus.AVAILABLE,
        // "1959",
        // "US",
        // "886976231214",
        // "12\"",
        // "Vinyl, LP",
        // "Legendary jazz album.",
        // "Miles Davis",
        // "MD001",
        // "Columbia",
        // "NM",
        // 5,
        // true,
        // 31999L,
        // 30000L,
        // 0,
        // 5,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("kind_of_blue.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "OK Computer",
        // ListingStatus.AVAILABLE,
        // "1997",
        // "UK",
        // "724384552719",
        // "12\"",
        // "Vinyl, LP",
        // "Alternative rock landmark.",
        // "Radiohead",
        // "RH001",
        // "Parlophone",
        // "NM",
        // 12,
        // true,
        // 30999L,
        // 29000L,
        // 500,
        // 8,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("ok_computer.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Rumours",
        // ListingStatus.AVAILABLE,
        // "1977",
        // "US",
        // "081227972187",
        // "12\"",
        // "Vinyl, LP",
        // "Classic pop rock album.",
        // "Fleetwood Mac",
        // "FM001",
        // "Warner Bros.",
        // "VG+",
        // 11,
        // true,
        // 26999L,
        // 25000L,
        // 0,
        // 9,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("rumours.jpg")),
        // new Listing(
        // UUID.randomUUID(),
        // "Discovery",
        // ListingStatus.AVAILABLE,
        // "2001",
        // "FR",
        // "724384960712",
        // "12\"",
        // "Vinyl, LP",
        // "French house classic.",
        // "Daft Punk",
        // "DP001",
        // "Virgin",
        // "NM",
        // 14,
        // true,
        // 29999L,
        // 28000L,
        // 1000,
        // 10,
        // new ArrayList<>(),
        // null,
        // null,
        // List.of("discovery.jpg")));

        // for (Listing listing : listings) {
        // listing.setOwner(user);
        // listingService.saveListing(listing);
        // }
        // }
}
