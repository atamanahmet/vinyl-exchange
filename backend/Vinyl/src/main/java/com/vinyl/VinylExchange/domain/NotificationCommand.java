package com.vinyl.VinylExchange.domain;

import java.util.UUID;

import com.vinyl.VinylExchange.domain.enums.NotificationType;

public record NotificationCommand(
        NotificationType type,
        String title,
        String message,
        UUID relatedListingId) {

}
