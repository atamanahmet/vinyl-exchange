package com.vinyl.VinylExchange.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.entity.Listing;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findAllByOwner_Id(UUID ownerId);

    List<Listing> findAllByIdIn(List<UUID> listingIds);

    List<Listing> findByPromoteTrue();

    List<Listing> findByOnHoldFalse();
}
