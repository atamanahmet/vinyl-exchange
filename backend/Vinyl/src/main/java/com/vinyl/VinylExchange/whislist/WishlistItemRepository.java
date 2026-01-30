package com.vinyl.VinylExchange.whislist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {

    Optional<WishlistItem> findByIdAndUserId(UUID WishlistItemId, UUID userId);

    Optional<WishlistItem> findByUserIdAndTitleAndArtist(UUID userId, String title,
            String artist);

    List<WishlistItem> findByUserId(UUID userId);

    List<WishlistItem> findByTitle(String title);

    List<WishlistItem> findByArtist(String artist);

    List<WishlistItem> findByTitleAndArtist(String title, String artist);

    boolean existsByUserIdAndTitleAndArtist(UUID userId, String title, String artist);

    boolean existsByUserIdAndTitleAndArtistAndYear(UUID userId, String title, String artist, Integer year);

    boolean existsByUserIdAndTitle(UUID userId, String title);

    boolean existsByUserIdAndArtist(UUID userId, String artist);

    // TODO: check if necessary, delete by wl id must be suffice
    void deleteByUserIdAndTitleAndArtist(UUID userId, String title, String artist);
}
