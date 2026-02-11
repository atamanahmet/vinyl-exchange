package com.vinyl.VinylExchange.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoverArtResponse(List<ImageMeta> images) {

}
