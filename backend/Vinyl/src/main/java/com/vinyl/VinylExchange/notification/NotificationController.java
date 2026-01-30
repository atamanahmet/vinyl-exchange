package com.vinyl.VinylExchange.notification;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.notification.dto.MarkAsReadResponse;
import com.vinyl.VinylExchange.notification.dto.NotificationResponse;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationResponse> getAllNotification(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        NotificationResponse notifications = notificationService.getAllNotificationsByUserId(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<NotificationResponse> getUnread(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        NotificationResponse notifications = notificationService.getUnreadNotifications(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<MarkAsReadResponse> markAsRead(
            @PathVariable(name = "notificationId") UUID notificationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        notificationService.markAsRead(notificationId, userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
