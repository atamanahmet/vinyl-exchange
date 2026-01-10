package com.vinyl.VinylExchange.listing;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.listing.enums.ListingStatus;

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

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Listing l " +
            "WHERE l.id = :listingId " +
            "AND l.stockQuantity > 0 " +
            "AND l.status = :status")
    boolean isAvailableForTrade(
            @Param("listingId") UUID listingId,
            @Param("status") ListingStatus status);
}
