package com.vinyl.VinylExchange.domain.dto;

import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

public record UserDTO(
                String username,
                String email) {

        public UserDTO(User user) {
                this(user.getUsername(), user.getEmail());
        }

        public UserDTO(UserPrincipal userPrincipal) {
                this(userPrincipal.getUsername(), userPrincipal.getEmail());
        }

}
