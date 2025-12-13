package com.vinyl.VinylExchange.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequestDTO(
        @NotNull UUID listingId,
        @Min(1) int quantity) {
}