package com.vinyl.VinylExchange.listing.dtos;

import jakarta.validation.constraints.NotNull;

public record FreezeRequest(@NotNull Boolean action) {
}
