package com.vinyl.VinylExchange.controller;

import java.io.IOException;

import com.vinyl.VinylExchange.service.ListingSearchQueryService;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.dto.ListingDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings/search")
public class ListingSearchController {

    private final ListingSearchQueryService listingSearchQueryService;

    @GetMapping
    public ResponseEntity<Page<ListingDTO>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws IOException {

        Page<ListingDTO> searchResult = listingSearchQueryService.search(query, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(searchResult);
    }
}