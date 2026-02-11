package com.vinyl.VinylExchange.domain;

import com.vinyl.VinylExchange.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutResult {

    private Boolean success;

    private Order order;

    private String message;

}
