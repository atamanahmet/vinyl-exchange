package com.vinyl.VinylExchange.shared.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Media {

    private String name;
    private int duration;
    private String format; // CD,Vinyletc.

    @JsonProperty("disc-count")
    private int discCount;
    @JsonProperty("track-count")
    private int trackCount;
}
