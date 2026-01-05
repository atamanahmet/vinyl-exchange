package com.vinyl.VinylExchange.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {

        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserPrincipal userPrincipal)
            throws JsonProcessingException {

        List<Order> orders = orderService.getAllOrdersByUserId(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orders);
    }
}
