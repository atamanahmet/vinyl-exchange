package com.vinyl.VinylExchange.cart;

import java.util.List;

import com.vinyl.VinylExchange.cart.dto.CartDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartValidationResult {

    private CartDTO cartDTO;
    private List<CartValidationIssue> issues;
    private boolean hasErrors;
}
