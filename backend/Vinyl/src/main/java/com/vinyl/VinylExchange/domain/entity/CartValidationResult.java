package com.vinyl.VinylExchange.domain.entity;

import java.util.List;

import com.vinyl.VinylExchange.domain.dto.CartDTO;
import com.vinyl.VinylExchange.domain.dto.CartValidationIssue;

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
