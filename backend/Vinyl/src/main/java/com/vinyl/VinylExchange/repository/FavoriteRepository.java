package com.vinyl.VinylExchange.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    boolean existsByUserIdAndListingId(UUID userId, UUID listingId);

    void deleteByUserIdAndListingId(UUID userId, UUID listingId);

    List<Favorite> findAllByUserId(UUID userId);
}
