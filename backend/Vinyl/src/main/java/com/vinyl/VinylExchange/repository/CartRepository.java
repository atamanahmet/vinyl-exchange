package com.vinyl.VinylExchange.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUser(User user);
}
