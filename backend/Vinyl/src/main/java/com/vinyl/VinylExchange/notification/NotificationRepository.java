package com.vinyl.VinylExchange.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserId(UUID userId);

    List<Notification> findByUserIdAndReadFalse(UUID userId);

    Optional<Notification> findByIdAndUserId(UUID notificationId, UUID userId);

    int countByUserIdAndReadFalse(UUID userId);
}
