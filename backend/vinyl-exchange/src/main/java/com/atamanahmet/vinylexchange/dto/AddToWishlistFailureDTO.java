package com.atamanahmet.vinylexchange.dto;

import lombok.Data;

@Data
public class AddToWishlistFailureDTO {
    private AddToWishlistRequest request;
    private String reason;
}
