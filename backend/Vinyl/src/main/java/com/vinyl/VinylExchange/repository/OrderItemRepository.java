package com.vinyl.VinylExchange.repository;

import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
