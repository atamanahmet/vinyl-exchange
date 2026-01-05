package com.vinyl.VinylExchange.shared.dto;

import java.util.UUID;

import com.vinyl.VinylExchange.shared.TradePreference;

public record TradePreferenceDTO(
        UUID id,
        String desiredItem,
        Double extraAmount,
        String paymentDirection) {

    public TradePreferenceDTO(TradePreference tradePreference) {
        this(
                tradePreference.getId(),
                tradePreference.getDesiredItem(),
                tradePreference.getExtraAmount(),
                tradePreference.getPaymentDirection());
    }
}
