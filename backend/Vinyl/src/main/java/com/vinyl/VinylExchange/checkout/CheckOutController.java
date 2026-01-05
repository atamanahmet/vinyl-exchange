package com.vinyl.VinylExchange.checkout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.checkout.dto.CheckOutresultDTO;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@RestController
@RequestMapping("/checkout")
public class CheckOutController {
    private final CheckOutService checkOutService;

    public CheckOutController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    @PostMapping
    public ResponseEntity<CheckOutresultDTO> checkout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CheckOutResult checkOutResult = checkOutService.proceedCheckOut(userPrincipal.getId());

        CheckOutresultDTO resultDTO = CheckOutresultDTO.builder()
                .success(checkOutResult.getSuccess())
                .message(checkOutResult.getMessage())
                .orderId(checkOutResult.getOrder().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultDTO);
    }
}
