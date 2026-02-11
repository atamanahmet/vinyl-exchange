package com.vinyl.VinylExchange.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    List<NotificationDTO> notifications;
    int unreadCount;
}
