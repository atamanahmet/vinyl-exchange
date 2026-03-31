package com.atamanahmet.vinylexchange.dto;

import java.util.UUID;

public record MarkAsReadResponse(
        UUID notificationId,
        int unreadCount) {
}