package com.vinyl.VinylExchange.domain.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Artist {
    private String id;
    private String name;

    // @JsonProperty("sort-name")
    // private String sortName;
}
