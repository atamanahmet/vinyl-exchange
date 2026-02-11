package com.vinyl.VinylExchange.dto;

import java.util.UUID;

public record MarkAsReadResponse(
        UUID notificationId,
        int unreadCount) {
}