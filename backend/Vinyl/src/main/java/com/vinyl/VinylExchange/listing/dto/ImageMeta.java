package com.vinyl.VinylExchange.listing.dto;

import java.util.Map;

public record ImageMeta(
                String image,
                boolean front,
                Map<String, String> thumbnails) {

}
