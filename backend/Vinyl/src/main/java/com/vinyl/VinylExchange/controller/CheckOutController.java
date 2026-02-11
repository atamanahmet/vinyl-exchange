package com.vinyl.VinylExchange.controller;

import com.vinyl.VinylExchange.domain.CheckOutResult;
import com.vinyl.VinylExchange.service.CheckOutService;
import com.vinyl.VinylExchange.session.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.dto.CheckOutresultDTO;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckOutController {

    private final CheckOutService checkOutService;

    @PostMapping
    public ResponseEntity<CheckOutresultDTO> checkout() {

        CheckOutResult checkOutResult = checkOutService.proceedCheckOut(UserUtil.getCurrentUserId());

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
