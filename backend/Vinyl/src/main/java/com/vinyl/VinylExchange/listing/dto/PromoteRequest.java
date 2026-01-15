package com.vinyl.VinylExchange.listing.dto;

import jakarta.validation.constraints.NotNull;

public record PromoteRequest(@NotNull Boolean action) {

}
