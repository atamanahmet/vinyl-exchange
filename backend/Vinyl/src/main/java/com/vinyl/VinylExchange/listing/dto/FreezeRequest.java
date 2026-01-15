package com.vinyl.VinylExchange.listing.dto;

import jakarta.validation.constraints.NotNull;

public record FreezeRequest(@NotNull Boolean action) {
}
