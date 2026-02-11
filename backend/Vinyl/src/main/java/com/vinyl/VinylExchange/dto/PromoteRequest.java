package com.vinyl.VinylExchange.dto;

import jakarta.validation.constraints.NotNull;

public record PromoteRequest(@NotNull Boolean action) {

}
