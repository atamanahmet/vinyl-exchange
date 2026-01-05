package com.vinyl.VinylExchange.auth.dto;

import com.vinyl.VinylExchange.user.dto.UserDTO;

public record AuthResponse(
        UserDTO userDTO,
        String token) {
}
