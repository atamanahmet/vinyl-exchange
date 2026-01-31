package com.vinyl.VinylExchange.notification;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Service;

@Service
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadFalse(UUID userId, Pageable pageable);

    Optional<Notification> findByIdAndUserId(UUID notificationId, UUID userId);

    int countByUserIdAndReadFalse(UUID userId);
}
