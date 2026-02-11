package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.UUID;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import com.vinyl.VinylExchange.domain.entity.Order;
import com.vinyl.VinylExchange.repository.OrderRepository;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.exception.OrderNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    // private final static Logger logger =
    // LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(
            OrderRepository orderRepository) {

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

    @Transactional
    public Order initializeOrder() {

        Order order = new Order();

        order.setOrderNumber(orderRepository.getNextOrderNumber());

        return orderRepository.save(order);
    }

    public Long getNextOrderNumber() {
        return orderRepository.getNextOrderNumber();
    }

}
