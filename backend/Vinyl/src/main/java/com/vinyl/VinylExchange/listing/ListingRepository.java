package com.vinyl.VinylExchange.listing;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {

    List<Listing> findAllByOwner_Id(UUID ownerId);

    List<Listing> findAllByIdIn(List<UUID> listingIds);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Listing> findByIdIn(List<UUID> listingIds);

    List<Listing> findByPromoteTrue();

    List<Listing> findByOnHoldFalse();

    boolean existsByTitle(String title);
}
