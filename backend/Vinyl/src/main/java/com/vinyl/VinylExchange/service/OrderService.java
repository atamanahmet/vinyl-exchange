package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Order;
import com.vinyl.VinylExchange.exception.OrderNotFoundException;
import com.vinyl.VinylExchange.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException());
    }

    public List<Order> getAllOrdersByUserId(UUID userId) {
        return orderRepository.findAllByBuyerId(userId);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
