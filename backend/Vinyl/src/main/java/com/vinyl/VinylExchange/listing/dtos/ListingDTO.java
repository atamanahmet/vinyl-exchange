package com.vinyl.VinylExchange.listing.dtos;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.vinyl.VinylExchange.config.json.DiscountDeserializer;
import com.vinyl.VinylExchange.config.json.DiscountSerializer;
import com.vinyl.VinylExchange.config.json.PriceKurusDeserializer;
import com.vinyl.VinylExchange.config.json.PriceTlSerializer;
import com.vinyl.VinylExchange.listing.Listing;
import com.vinyl.VinylExchange.shared.dto.TradePreferenceDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingDTO {

    private UUID id;
    private String title;
    private String status;
    private String barcode;

    private String packaging;

    @JsonProperty("price")
    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long priceKurus;

    @JsonDeserialize(using = PriceKurusDeserializer.class)
    @JsonSerialize(using = PriceTlSerializer.class)
    private long discountedPrice;

    private Boolean tradeable;
    private List<String> imagePaths;
    private List<TradePreferenceDTO> tradePreferences;
    private String format;
    private String description;
    private String date;
    private Integer trackCount;
    private long tradeValue;

    @Min(0)
    @Max(10_000)
    @JsonProperty("discount")
    @JsonDeserialize(using = DiscountDeserializer.class)
    @JsonSerialize(using = DiscountSerializer.class)
    private int discountBP; // bp

    private String condition;
    private String artistName;
    private String labelName;

    public ListingDTO(Listing listing, List<TradePreferenceDTO> tradePreferenceDTOs) {
        this.id = listing.getId();
        this.title = listing.getTitle();
        this.description = listing.getDescription();
        this.date = listing.getDate();
        this.priceKurus = listing.getPriceKurus();
        this.tradeable = listing.getTradeable();
        this.discountedPrice = listing.getDiscountedPriceKurus();
        this.imagePaths = listing.getImagePaths();
        this.format = listing.getFormat();
        this.packaging = listing.getPackaging();
        this.trackCount = listing.getTrackCount();
        this.tradePreferences = tradePreferenceDTOs;
        this.discountBP = listing.getDiscountBP();
        this.barcode = listing.getBarcode();
        this.artistName = listing.getArtistName();
        this.condition = listing.getCondition();
        this.labelName = listing.getLabelName();

    }
}
