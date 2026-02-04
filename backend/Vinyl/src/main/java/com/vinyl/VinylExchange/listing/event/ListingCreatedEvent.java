package com.vinyl.VinylExchange.listing.event;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingCreatedEvent {
    private final UUID listingId;
    private final String title;
    private final String artist;
    private final int date;
}
