package com.vinyl.VinylExchange.domain.dto;

import java.util.List;
import java.util.UUID;

public record CartListingsDTO(List<UUID> listingIdList) {
}
