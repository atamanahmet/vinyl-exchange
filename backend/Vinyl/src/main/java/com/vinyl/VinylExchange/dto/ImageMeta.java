package com.vinyl.VinylExchange.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageMeta(
        String image,
        boolean front,
        Map<String, String> thumbnails) {

}
