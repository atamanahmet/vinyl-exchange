package com.vinyl.VinylExchange.domain.dto;

import com.vinyl.VinylExchange.domain.entity.User;

public record UserResponseDTO(
                String username,
                String email) {

        public UserResponseDTO(User user) {
                this(user.getUsername(), user.getEmail());
        }
}
