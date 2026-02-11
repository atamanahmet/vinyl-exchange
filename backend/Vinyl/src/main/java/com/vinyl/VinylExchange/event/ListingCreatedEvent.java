package com.vinyl.VinylExchange.event;

import com.vinyl.VinylExchange.domain.entity.Listing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingCreatedEvent {
    private final Listing listing;
}
