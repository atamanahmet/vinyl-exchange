package com.vinyl.VinylExchange.whislist;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.shared.exception.ResourceNotFoundException;
import com.vinyl.VinylExchange.shared.exception.UnauthorizedActionException;
import com.vinyl.VinylExchange.user.User;
import com.vinyl.VinylExchange.user.UserService;
import com.vinyl.VinylExchange.whislist.dto.AddToWishlistRequest;
import com.vinyl.VinylExchange.whislist.dto.WishlistItemDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserService userService;

    public List<WishlistItemDTO> getWishlistByUserId(UUID userId) {
        List<WishlistItem> wishlists = wishlistItemRepository.findByUserId(userId);
        return wishlists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // checks item year only while creation,
    // notification will send even when release year is diffirent
    @Transactional
    public WishlistItemDTO addToWishlist(UUID userId, AddToWishlistRequest request) {

        String title = request.getTitle();
        String artist = request.getArtist();
        Integer year = request.getYear();

        if (wishlistItemRepository.existsByUserIdAndTitleAndArtistAndYear(
                userId, title, artist, year)) {

            // TODO: add duplicate exception
            throw new ResourceNotFoundException("This item is already in your wishlist");
        }

        User user = userService.findByUserId(userId);

        WishlistItem wishlistItem = new WishlistItem();

        wishlistItem.setUser(user);
        wishlistItem.setTitle(title);
        wishlistItem.setArtist(artist);
        wishlistItem.setYear(request.getYear());
        wishlistItem.setImageUrl(request.getImageUrl());
        wishlistItem.setFormat(request.getFormat());

        WishlistItem savedWishlistItem = wishlistItemRepository.save(wishlistItem);

        return convertToDTO(savedWishlistItem);
    }

    @Transactional
    public void removeFromWishlist(UUID userId, Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        User user = userService.findByUserId(userId);

        if (!wishlistItem.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("User don't have permission to delete this wishlist item");
        }

        wishlistItemRepository.delete(wishlistItem);
    }

    public boolean isInWishlist(UUID userId, String title, String artist, Integer year) {

        return wishlistItemRepository.existsByUserIdAndTitleAndArtistAndYear(
                userId, title, artist, year);
    }

    // no year check, send notification
    public List<WishlistItem> findMatchingWishlists(String title, String artist) {

        return wishlistItemRepository.findByTitleAndArtist(title, artist);
    }

    private WishlistItemDTO convertToDTO(WishlistItem wishlistItem) {
        return WishlistItemDTO.builder()
                .id(wishlistItem.getId())
                .title(wishlistItem.getTitle())
                .artist(wishlistItem.getArtist())
                .year(wishlistItem.getYear())
                .format(wishlistItem.getFormat())
                .addedAt(wishlistItem.getAddedAt())
                .build();
    }
}
