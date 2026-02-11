package com.vinyl.VinylExchange.dto;

import com.vinyl.VinylExchange.domain.entity.User;

import com.vinyl.VinylExchange.security.principal.UserDetailsImpl;

public record UserDTO(
                String username,
                String email) {

        public UserDTO(User user) {
                this(user.getUsername(), user.getEmail());
        }

        public UserDTO(UserDetailsImpl userPrincipal) {
                this(userPrincipal.getUsername(), userPrincipal.getEmail());
        }

}
