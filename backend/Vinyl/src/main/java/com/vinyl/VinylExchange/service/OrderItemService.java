package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.OrderItem;
import com.vinyl.VinylExchange.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {

        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {

        return orderItemRepository.save(orderItem);
    }

    public OrderItem saveOrderItemAndFlush(OrderItem orderItem) {

        return orderItemRepository.saveAndFlush(orderItem);
    }

    public Optional<OrderItem> getOrderItemById(UUID orderItemId) {
        return orderItemRepository.findById(orderItemId);
    }

    public List<OrderItem> saveAllOrderItems(List<OrderItem> orderItems) {
        return orderItemRepository.saveAll(orderItems);
    }

    public void removeOrderItem(UUID orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }
}
