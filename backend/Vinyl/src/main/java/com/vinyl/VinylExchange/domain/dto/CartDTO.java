package com.vinyl.VinylExchange.domain.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vinyl.VinylExchange.config.json.DiscountDeserializer;
import com.vinyl.VinylExchange.config.json.DiscountSerializer;
import com.vinyl.VinylExchange.config.json.PriceKurusDeserializer;
import com.vinyl.VinylExchange.config.json.PriceTlSerializer;
import com.vinyl.VinylExchange.domain.entity.CartValidationIssue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {

    private UUID cartId;

    private List<CartItemDTO> items;

    @JsonProperty("price")
    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long totalPriceKurus; // samllest unit, cent/kurus

    @JsonProperty("discountedPrice")
    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long totalDiscountedPriceKurus; // samllest unit, cent/kurus

    @Min(0)
    @Max(10_000)
    @JsonProperty("discount")
    @JsonDeserialize(using = DiscountDeserializer.class)
    @JsonSerialize(using = DiscountSerializer.class)
    private int totalDiscountBP; // as basisPoint, /10_000

    private int totalItems;

    private List<CartValidationIssue> validationIssues;

    // todo calculate total discount

}
