package com.vinyl.VinylExchange.notification;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.notification.dto.NotificationDTO;
import com.vinyl.VinylExchange.notification.dto.NotificationResponse;

import com.vinyl.VinylExchange.user.UserService;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {

        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Transactional
    public void notifyUsers(List<UUID> userIds, NotificationCommand command) {
        List<Notification> notifications = userIds.stream()
                .map(userId -> createNotification(userId, command))
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public Notification createNotification(
            UUID userId,
            NotificationCommand command) {

        Notification notification = Notification.builder()
                .userId(userId)
                .title(command.title())
                .message(command.message())
                .type(command.type())
                .relatedListingId(command.relatedListingId())
                .build();

        return notificationRepository.save(notification);
    }

    public NotificationResponse getUnreadNotifications(UUID userId) {

        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);

        List<NotificationDTO> notificationDTOs = convertToDTO(notifications);

        return new NotificationResponse(notificationDTOs, notificationDTOs.size());
    }

    public NotificationResponse getAllNotificationsByUserId(UUID userId) {

        List<Notification> notifications = notificationRepository.findAllByUserId(userId);

        List<NotificationDTO> notificationDTOs = convertToDTO(notifications);

        int unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

        return new NotificationResponse(notificationDTOs, unreadCount);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {

        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    public List<NotificationDTO> convertToDTO(List<Notification> notifications) {

        return notifications.stream().map(notification -> NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .relatedListingId(notification.getRelatedListingId())
                .build())
                .toList();
    }
}