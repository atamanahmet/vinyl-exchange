package com.vinyl.VinylExchange.domain.dto;

import jakarta.validation.constraints.NotNull;

public record FreezeRequest(@NotNull Boolean action) {

}
