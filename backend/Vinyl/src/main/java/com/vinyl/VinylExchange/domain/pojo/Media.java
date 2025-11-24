package com.vinyl.VinylExchange.domain.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Media {
    private String name;
    private int duration;
    private String format; // e.g., "CD", "Vinyl"

    @JsonProperty("disc-count")
    private int discCount;
    @JsonProperty("track-count")
    private int trackCount;
}
