package com.vinyl.VinylExchange.notification;

import java.util.UUID;

import com.vinyl.VinylExchange.notification.enums.NotificationType;

public record NotificationCommand(
        NotificationType type,
        String title,
        String message,
        UUID relatedListingId) {

}
