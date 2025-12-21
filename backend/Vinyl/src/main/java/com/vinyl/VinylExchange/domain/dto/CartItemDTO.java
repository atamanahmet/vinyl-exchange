package com.vinyl.VinylExchange.domain.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vinyl.VinylExchange.config.json.DiscountDeserializer;
import com.vinyl.VinylExchange.config.json.DiscountSerializer;
import com.vinyl.VinylExchange.config.json.PriceKurusDeserializer;
import com.vinyl.VinylExchange.config.json.PriceTlSerializer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private UUID id;

    private UUID listingId;

    @JsonProperty("quantity")
    private int orderQuantity;

    private boolean committed;

    private String title;

    @JsonProperty("unitPrice")
    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long pricePerUnit;

    @JsonProperty("totalPrice")
    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long itemTotalPriceKurus;

    @Min(0)
    @Max(10_000)
    @JsonProperty("discountPerUnit")
    @JsonDeserialize(using = DiscountDeserializer.class)
    @JsonSerialize(using = DiscountSerializer.class)
    private int discountPerUnit; // as basisPoint, /10_000

    @JsonSerialize(using = PriceTlSerializer.class)
    private long discountedTotalPrice;

    private String mainImagePath;

}
