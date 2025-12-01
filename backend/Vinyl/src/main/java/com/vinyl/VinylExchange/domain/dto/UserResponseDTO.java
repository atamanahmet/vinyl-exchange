package com.vinyl.VinylExchange.domain.dto;

import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

public record UserResponseDTO(
                String username,
                String email) {

        public UserResponseDTO(User user) {
                this(user.getUsername(), user.getEmail());
        }

        public UserResponseDTO(UserPrincipal userPrincipal) {
                this(userPrincipal.getUsername(), userPrincipal.getEmail());
        }

}
