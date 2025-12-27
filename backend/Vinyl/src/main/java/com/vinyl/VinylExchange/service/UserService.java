package com.vinyl.VinylExchange.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.domain.entity.UserStatus;
import com.vinyl.VinylExchange.domain.entity.UserStatusHistory;
import com.vinyl.VinylExchange.exception.InvalidStatusTransitionException;
import com.vinyl.VinylExchange.repository.UserRepository;
import com.vinyl.VinylExchange.repository.UserStatusHistoryRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusHistoryRepository statusHistoryRepository;

    public UserService(UserRepository userRepository, UserStatusHistoryRepository statusHistoryRepository) {

        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    public User changeUserStatus(UUID userId, UserStatus newStatus, UUID changedById, String changedBy, String reason) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist"));

        UserStatus oldStatus = user.getStatus();

        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(oldStatus, newStatus);
        }

        user.setStatus(newStatus);

        User saveDUser = userRepository.save(user);

        UserStatusHistory history = UserStatusHistory.builder()
                .userId(userId)
                .status(newStatus)
                .previousStatus(oldStatus)
                .changedAt(LocalDateTime.now())
                .changedBy(changedBy)
                .changedById(changedById)
                .reason(reason).build();

        statusHistoryRepository.save(history);

        return saveDUser;

    }

}
