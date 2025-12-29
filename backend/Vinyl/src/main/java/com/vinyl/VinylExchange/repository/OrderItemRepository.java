package com.vinyl.VinylExchange.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinyl.VinylExchange.domain.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
