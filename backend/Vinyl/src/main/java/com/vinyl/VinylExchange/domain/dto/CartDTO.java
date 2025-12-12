package com.vinyl.VinylExchange.domain.dto;

import java.util.List;

import com.vinyl.VinylExchange.domain.entity.Listing;

public record CartDTO(List<Listing> cartListings) {

}
