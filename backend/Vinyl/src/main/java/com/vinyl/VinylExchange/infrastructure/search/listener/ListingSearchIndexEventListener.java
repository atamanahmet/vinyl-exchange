package com.vinyl.VinylExchange.infrastructure.search.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.vinyl.VinylExchange.infrastructure.search.service.ListingIndexService;
import com.vinyl.VinylExchange.listing.event.ListingCreatedEvent;
import com.vinyl.VinylExchange.listing.event.ListingUpdatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListingSearchIndexEventListener {

    private final ListingIndexService listingIndexService;

    @Async
    @TransactionalEventListener(classes = ListingCreatedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void onListingCreated(ListingCreatedEvent creationEvent) {
        listingIndexService.indexListing(creationEvent.getListing());
    }

    @Async
    @TransactionalEventListener(classes = ListingUpdatedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void onListingUpdated(ListingUpdatedEvent updateEvent) {
        listingIndexService.indexListing(updateEvent.getListing());
    }

}
