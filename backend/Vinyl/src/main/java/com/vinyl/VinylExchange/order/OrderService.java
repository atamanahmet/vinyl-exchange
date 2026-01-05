package com.vinyl.VinylExchange.order;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.shared.exception.OrderNotFoundException;

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

    public Order initializeOrder() {

        Order order = new Order();

        order.setOrderNumber(orderRepository.getNextOrderNumber());

        return orderRepository.save(order);
    }
}
