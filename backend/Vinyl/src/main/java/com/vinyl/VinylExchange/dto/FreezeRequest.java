package com.vinyl.VinylExchange.dto;

import jakarta.validation.constraints.NotNull;

public record FreezeRequest(@NotNull Boolean action) {
}
