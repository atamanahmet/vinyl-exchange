package com.vinyl.VinylExchange.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist {
    String id;
    String name;
    String disambiguation;
}
