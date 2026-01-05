package com.vinyl.VinylExchange.cart;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    // todo: analytics
    // most popular items
    // count by listing id etc.
}
