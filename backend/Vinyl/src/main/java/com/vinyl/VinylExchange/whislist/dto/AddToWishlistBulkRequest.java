package com.vinyl.VinylExchange.whislist.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddToWishlistBulkRequest {
    private List<AddToWishlistRequest> bulkRequest;
}
