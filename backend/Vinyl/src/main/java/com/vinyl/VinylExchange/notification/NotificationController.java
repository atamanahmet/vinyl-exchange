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

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
        private final NotificationService notificationService;

        @GetMapping
        public ResponseEntity<NotificationResponse> getNotifications(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                NotificationResponse notifications = notificationService
                                .getAllNotificationsByUserId(userPrincipal.getId(), pageable);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(notifications);
        }

        @GetMapping("/dropdown")
        public ResponseEntity<NotificationResponse> getDropdownNotifications(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                NotificationResponse notifications = notificationService
                                .getAllNotificationsByUserId(userPrincipal.getId(), pageable);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(notifications);
        }

        @GetMapping("/unread")
        public ResponseEntity<NotificationResponse> getUnread(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                NotificationResponse notifications = notificationService.getUnreadNotifications(userPrincipal.getId(),
                                pageable);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(notifications);
        }

        @PostMapping("/{notificationId}/read")
        public ResponseEntity<MarkAsReadResponse> markAsRead(
                        @PathVariable(name = "notificationId") UUID notificationId,
                        @AuthenticationPrincipal UserPrincipal userPrincipal) {

                System.out.println("read request: " + notificationId);

                notificationService.markAsRead(notificationId, userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }
}
