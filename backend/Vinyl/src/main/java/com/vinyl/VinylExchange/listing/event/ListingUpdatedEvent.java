package com.vinyl.VinylExchange.listing.event;

import com.vinyl.VinylExchange.listing.Listing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingUpdatedEvent {
    private final Listing listing;
}
