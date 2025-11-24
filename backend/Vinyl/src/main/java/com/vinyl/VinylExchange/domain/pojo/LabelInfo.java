package com.vinyl.VinylExchange.domain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabelInfo {

    @JsonProperty("catalog-number")
    private String catalogNumber;

    private Label label;
}