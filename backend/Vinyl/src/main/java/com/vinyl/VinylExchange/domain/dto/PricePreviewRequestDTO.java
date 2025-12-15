package com.vinyl.VinylExchange.domain.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PricePreviewRequestDTO(
        @NotNull @Digits(integer = 10, fraction = 2) BigDecimal priceTL,

        @NotNull @Min(0) @Max(100) Integer discountPercent) {

}
