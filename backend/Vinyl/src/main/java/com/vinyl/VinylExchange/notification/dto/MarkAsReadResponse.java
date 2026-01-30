package com.vinyl.VinylExchange.notification.dto;

import java.util.UUID;

public record MarkAsReadResponse(
        UUID notificationId,
        int unreadCount) {
}