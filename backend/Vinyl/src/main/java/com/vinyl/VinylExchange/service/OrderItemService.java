package com.vinyl.VinylExchange.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.OrderItem;
import com.vinyl.VinylExchange.repository.OrderItemRepository;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemById(UUID orderItemId) {
        // todo hadnle exception
        return orderItemRepository.findById(orderItemId).orElseGet(null);
    }
}
