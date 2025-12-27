package com.vinyl.VinylExchange.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.entity.UserStatusHistory;

@Repository
public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, UUID> {
    Optional<UserStatusHistory> findByUserId(UUID userId);
}
