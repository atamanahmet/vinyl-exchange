package com.vinyl.VinylExchange.repository;

import java.util.List;
import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.enums.ListingStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {

        List<Listing> findAllByOwner_IdAndStatus(UUID ownerId, ListingStatus status);

        List<Listing> findAllByIdIn(List<UUID> listingIds);

        @Lock(LockModeType.PESSIMISTIC_READ)
        @Query("SELECT l FROM Listing l WHERE l.id IN :listingIds")
        List<Listing> findByIdInWithLock(List<UUID> listingIds);

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

        @Query("SELECT l FROM Listing l " +
                        "WHERE l.stockQuantity > 0 " +
                        "AND l.status = :status " +
                        "AND l.onHold = false")
        Page<Listing> findAllWithStatus(@Param("status") ListingStatus status, Pageable pageable);

        @Query("SELECT COUNT(l) FROM Listing l " +
                        "WHERE l.stockQuantity > 0 " +
                        "AND l.status = :status " +
                        "AND l.onHold = false")
        long countListingsWithStatus(@Param("status") ListingStatus status);

        Page<Listing> findAll(Pageable pageable);
}
