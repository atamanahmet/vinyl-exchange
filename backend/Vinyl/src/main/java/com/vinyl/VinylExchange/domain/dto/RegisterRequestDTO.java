package com.vinyl.VinylExchange.domain.dto;

import com.vinyl.VinylExchange.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Username must not be blank") @Size(min = 5, max = 50) String username,
        @NotBlank(message = "Password must not be blank") @StrongPassword String password,
        @NotBlank(message = "Email must be valid") @Email String email) {

    public RegisterRequestDTO {
        username = username.trim();
        email = email.trim().toLowerCase();
    }
}