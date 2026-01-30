package com.vinyl.VinylExchange.whislist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddToWishlistRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Artist is required")
    private String artist;

    private Integer year;

    private String imageUrl;

    private String format;
}
