package com.vinyl.VinylExchange.domain.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vinyl.VinylExchange.domain.entity.Listing;

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
    private Double price;
    private Boolean tradeable;
    private List<String> imagePaths;
    private List<TradePreferenceDTO> tradePreferences;
    private String format;
    private String date;
    private Integer trackCount;
    private Double tradeValue;
    private Double discount;

    public ListingDTO(Listing listing, List<TradePreferenceDTO> tradePreferenceDTOs) {
        this.id = listing.getId();
        this.title = listing.getTitle();
        this.date = listing.getDate();
        this.price = listing.getPrice();
        this.tradeable = listing.getTradeable();
        this.imagePaths = listing.getImagePaths();
        this.format = listing.getFormat();
        this.trackCount = listing.getTrackCount();
        this.tradePreferences = tradePreferenceDTOs;
        this.discount = listing.getDiscount();

    }
}
