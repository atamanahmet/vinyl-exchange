package com.vinyl.VinylExchange.dto;

public record AuthResponse(
        UserDTO userDTO,
        String token) {
}
