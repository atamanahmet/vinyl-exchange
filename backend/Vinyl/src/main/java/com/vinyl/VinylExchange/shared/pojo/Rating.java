package com.vinyl.VinylExchange.shared.pojo;

import lombok.Data;

@Data
public class Rating {
    private Double value; // average rating
    private Integer count; // number of ratings
}