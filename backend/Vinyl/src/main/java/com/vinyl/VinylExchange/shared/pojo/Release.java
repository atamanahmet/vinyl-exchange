package com.vinyl.VinylExchange.shared.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinyl.VinylExchange.listing.label.LabelInfo;

import lombok.Data;

@Data
public class Release {

    private String id;
    private String title;
    private String date;
    private String country;
    private String barcode;
    private String packaging;
    private String format;

    @JsonProperty("artist-credit")
    private List<ArtistCredit> artistCredit;

    private List<Media> media;

    @JsonProperty("label-info")
    private List<LabelInfo> labelInfo;

    private Rating rating;
}
