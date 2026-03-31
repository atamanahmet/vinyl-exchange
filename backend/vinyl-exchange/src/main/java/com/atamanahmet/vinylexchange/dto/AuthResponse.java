package com.atamanahmet.vinylexchange.dto;

public record AuthResponse(
        UserDTO userDTO,
        String token) {
}
