package com.atamanahmet.vinylexchange.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddToWishlistBulkRequest {
    private List<AddToWishlistRequest> bulkRequest;
}
