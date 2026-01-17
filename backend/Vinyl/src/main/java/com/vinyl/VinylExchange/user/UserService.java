package com.vinyl.VinylExchange.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.shared.exception.InvalidStatusTransitionException;
import com.vinyl.VinylExchange.shared.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.shared.exception.UserNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusHistoryRepository statusHistoryRepository;

    public UserService(UserRepository userRepository, UserStatusHistoryRepository statusHistoryRepository) {

        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional
    public User changeUserStatus(
            UUID userId,
            UserStatus newStatus,
            UUID changedById,
            String changedBy,
            String reason) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

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

    public boolean existByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new NoCurrentUserException());
    }

    public Optional<User> findAdmin() {

        return userRepository.findByUsername("admin");
    }

    public User findByUserId(UUID userId) {

        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
    }

    public String findUsernameByUserId(UUID userId) {

        return userRepository.findUsernameById(userId).orElseThrow(() -> new UserNotFoundException());
    }

    public String findUsernameById(UUID userId) {
        return userRepository.findById(userId).get().getUsername();
    }
}
