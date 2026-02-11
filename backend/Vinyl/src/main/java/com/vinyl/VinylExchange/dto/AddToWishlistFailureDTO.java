package com.vinyl.VinylExchange.dto;

import lombok.Data;

@Data
public class AddToWishlistFailureDTO {
    private AddToWishlistRequest request;
    private String reason;
}
