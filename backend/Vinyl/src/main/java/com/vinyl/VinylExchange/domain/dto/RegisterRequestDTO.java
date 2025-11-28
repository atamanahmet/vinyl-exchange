package com.vinyl.VinylExchange.domain.dto;

import com.vinyl.VinylExchange.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Username must not be blank") @Size(min = 5, max = 50) String username,

        @NotBlank(message = "Password must not be blank") @Size(message = "Password must be minimum 8 maximum 64 character", min = 8, max = 64) @StrongPassword String password,

        @NotBlank(message = "Email must not be blank") @Email(message = "Email must be valid") @Size(max = 255, message = "Email must not exceed 255 characters") String email) {

    public RegisterRequestDTO {
        username = username != null ? username.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
    }
}