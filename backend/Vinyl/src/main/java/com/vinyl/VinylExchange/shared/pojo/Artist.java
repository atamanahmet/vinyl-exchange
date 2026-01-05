package com.vinyl.VinylExchange.shared.pojo;

import lombok.Data;

@Data
public class Artist {

    private String id;
    private String name;

    // @JsonProperty("sort-name")
    // private String sortName;
}
