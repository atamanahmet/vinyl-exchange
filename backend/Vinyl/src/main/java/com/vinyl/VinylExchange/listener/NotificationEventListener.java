package com.vinyl.VinylExchange.listener;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.event.ListingCreatedEvent;
import com.vinyl.VinylExchange.domain.NotificationCommand;
// import com.vinyl.VinylExchange.order.event.OrderPlacedEvent;
import com.vinyl.VinylExchange.service.NotificationService;
import com.vinyl.VinylExchange.domain.enums.NotificationType;
import com.vinyl.VinylExchange.domain.entity.WishlistItem;
import com.vinyl.VinylExchange.service.WishlistService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notificationService;
    private final WishlistService wishlistService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onListingCreated(ListingCreatedEvent event) {
        Listing listing = event.getListing();

        log.info("notification event triggered: {}", listing.getTitle());

        List<WishlistItem> matchingWishlists = wishlistService.findMatchingWishlists(
                listing.getTitle(),
                listing.getArtistName());

        log.info("found {} matching wishlist items", matchingWishlists.size());

        if (!matchingWishlists.isEmpty()) {

            List<UUID> userIdsToNotify = matchingWishlists.stream()
                    .map(wishlistItem -> wishlistItem.getUser().getId())
                    .distinct()
                    .filter(userId -> !userId.equals(listing.getOwnerId()))
                    .toList();

            log.info("notification will send to: {}", userIdsToNotify.get(0));

            if (!userIdsToNotify.isEmpty()) {
                NotificationCommand command = new NotificationCommand(
                        NotificationType.WISHLIST_ITEM_AVAILABLE,
                        "Wishlist Item Available!",
                        String.format("%s by %s is now available from %s",
                                listing.getTitle(),
                                listing.getArtistName(),
                                listing.getOwnerUsername()),
                        listing.getId());

                notificationService.notifyUsers(userIdsToNotify, command);
            }
        }
    }
}
