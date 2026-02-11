package com.vinyl.VinylExchange.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vinyl.VinylExchange.domain.entity.Favorite;
import com.vinyl.VinylExchange.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {

        this.favoriteRepository = favoriteRepository;
    }

    public void addToFavorites(UUID userId, UUID listingId) {

        boolean isExist = favoriteRepository.existsByUserIdAndListingId(userId, listingId);

        if (!isExist) {

            Favorite favorite = Favorite.builder()
                    .userId(userId)
                    .listingId(listingId)
                    .build();

            favoriteRepository.save(favorite);
        }
    }

    public void removeFromFavorites(UUID userId, UUID listingId) {

        favoriteRepository.deleteByUserIdAndListingId(userId, listingId);
    }

    public Set<UUID> getUserFavorites(UUID userId) {

        return favoriteRepository.findAllByUserId(userId) // list
                .stream()
                .map(favorite -> favorite.getListingId())
                .collect(Collectors.toSet()); // to set fro frontend
    }

    public boolean isFavorited(UUID userId, UUID listingId) {

        return favoriteRepository.existsByUserIdAndListingId(userId, listingId);
    }

}
