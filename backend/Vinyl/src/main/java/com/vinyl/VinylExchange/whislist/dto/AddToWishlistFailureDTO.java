package com.vinyl.VinylExchange.whislist.dto;

import lombok.Data;

@Data
public class AddToWishlistFailureDTO {
    private AddToWishlistRequest request;
    private String reason;
}
