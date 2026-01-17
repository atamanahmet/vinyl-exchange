package com.vinyl.VinylExchange.listing;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinyl.VinylExchange.cart.CartService;
import com.vinyl.VinylExchange.listing.dto.ListingDTO;
import com.vinyl.VinylExchange.listing.enums.ListingStatus;
import com.vinyl.VinylExchange.shared.dto.TradePreferenceDTO;
import com.vinyl.VinylExchange.shared.exception.InsufficientStockException;
import com.vinyl.VinylExchange.shared.exception.ListingNotFoundException;
import com.vinyl.VinylExchange.shared.exception.UnauthorizedActionException;
import com.vinyl.VinylExchange.user.User;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.shared.FileStorageService;
import com.vinyl.VinylExchange.shared.TradePreference;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ListingService {
    private final ListingRepository listingRepository;
    private FileStorageService fileStorageService;
    private final CartService cartService;

    public ListingService(
            ListingRepository listingRepository,
            FileStorageService fileStorageService,
            @Lazy CartService cartService) {

        this.listingRepository = listingRepository;
        this.fileStorageService = fileStorageService;
        this.cartService = cartService;
    }

    public List<Listing> getAllListings() {

        return listingRepository.findAll();
    }

    public List<Listing> getAllListingDTO() {

        return listingRepository.findAll();
    }

    public List<Listing> getAvailableListings() {

        return listingRepository.findByOnHoldFalse();
    }

    public List<Listing> getPromotedListings() {

        return listingRepository.findByPromoteTrue();
    }

    public List<Listing> getFilteredPromotedListings(UUID userId) {

        Set<UUID> cartListingIds = cartService
                .getCartDTO(userId)
                .getItems()
                .stream()
                .map(item -> item.getListingId()).collect(Collectors.toSet());
        if (cartListingIds.isEmpty()) {
            return null;
        }

        List<Listing> promotedListings = listingRepository.findByPromoteTrue();

        // only first 3 item to fit in cartPage
        List<Listing> filteredPromotedList = promotedListings.stream()
                .filter(listing -> !cartListingIds.contains(listing.getId()))
                .limit(3)
                .collect(Collectors.toList());

        return filteredPromotedList;
    }

    public void saveAllListing(List<Listing> listingList) {
        listingRepository.saveAll(listingList);
    }

    public List<Listing> getListingsByIds(List<UUID> listingIds) {

        return listingRepository.findAllByIdIn(listingIds);
    }

    public List<Listing> getListingsByIdsWithLock(List<UUID> listingIds) {

        return listingRepository.findAllByIdIn(listingIds);
    }

    public List<ListingDTO> getListingsDTOs() {

        List<Listing> allListings = listingRepository.findAll();

        List<ListingDTO> listingDTOs = new ArrayList<>();

        for (Listing listing : allListings) {
            List<TradePreferenceDTO> tradePreferenceDTOs = new ArrayList<>();

            for (TradePreference tradePreference : listing.getTradePreferences()) {

                tradePreferenceDTOs.add(new TradePreferenceDTO(tradePreference));
            }

            List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

            listingDTOs.add(new ListingDTO(listing, tradePreferenceDTOs, imagePaths));
        }

        return listingDTOs;
    }

    public Listing saveListing(Listing listing) {

        return listingRepository.save(listing);
    }

    public void deleteListing(UUID listingId, UserPrincipal userPrincipal) {

        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException());

        if (listing.getOwner().getId().equals(userPrincipal.getId())) {

            fileStorageService.deleteListingImages(listingId); // Clean up files

            listingRepository.deleteById(listingId);
        }
    }

    public Listing findListingById(UUID listingId) {

        return listingRepository
                .findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());
    }

    @Transactional
    public void createNewListing(
            String listingJson,
            List<MultipartFile> images,
            User user) {

        try {

            Listing listing = new ObjectMapper().readValue(listingJson, Listing.class);

            listing.setOwner(user);

            if (listing.getTradePreferences() != null) {

                listing.getTradePreferences()
                        .forEach(preference -> preference.setListing(listing));
            }

            Listing savedListing = listingRepository.save(listing);

            if (images != null) {
                fileStorageService.saveImages(images, savedListing.getId());
            }

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateListing(UUID id, String listingJson, List<MultipartFile> images, UUID userId) {

        Listing existingListing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException());

        // sec check, redundant but whatev
        if (!existingListing.getOwner().getId().equals(userId)) {
            throw new UnauthorizedActionException("Listing update unauthorized, action caused by userId: " + userId);
        }

        Listing updatedListing = new Listing();

        try {
            updatedListing = new ObjectMapper().readValue(listingJson, Listing.class);
            copyNonNullFields(updatedListing, existingListing);

            if (images != null && !images.isEmpty()) {
                fileStorageService.saveImages(images, id); // Add to existing folder
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        existingListing.getTradePreferences().clear();

        if (updatedListing.getTradePreferences() != null) {

            updatedListing.getTradePreferences().forEach(pref -> {
                pref.setListing(existingListing);
                existingListing.getTradePreferences().add(pref);
            });
        }

        saveListing(existingListing);
    }

    public List<Listing> getAllListingsByUserId(UUID ownerId) {

        return listingRepository.findAllByOwner_Id(ownerId);
    }

    public List<ListingDTO> getListingDTOsByUserId(UUID ownerId) {

        List<Listing> userListings = listingRepository.findAllByOwner_Id(ownerId);

        List<ListingDTO> listingDTOs = new ArrayList<>();

        for (Listing listing : userListings) {
            List<TradePreferenceDTO> tradePreferenceDTOs = new ArrayList<>();

            for (TradePreference tradePreference : listing.getTradePreferences()) {

                tradePreferenceDTOs.add(new TradePreferenceDTO(tradePreference));
            }

            List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

            listingDTOs.add(new ListingDTO(listing, tradePreferenceDTOs, imagePaths));
        }

        return listingDTOs;
    }

    public ListingDTO getListingDTOById(UUID listingId) {

        Listing listing = listingRepository
                .findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());

        List<TradePreferenceDTO> tradePreferenceDTOs = new ArrayList<>();

        for (TradePreference tradePreference : listing.getTradePreferences()) {

            tradePreferenceDTOs.add(new TradePreferenceDTO(tradePreference));

        }

        List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

        ListingDTO listingDTO = new ListingDTO(listing, tradePreferenceDTOs, imagePaths);

        return listingDTO;
    }

    public static void copyNonNullFields(Listing source, Listing target) {
        BeanWrapper sourceWrap = new BeanWrapperImpl(source);
        BeanWrapper targetWrap = new BeanWrapperImpl(target);

        for (PropertyDescriptor propertyDescriptor : sourceWrap.getPropertyDescriptors()) {
            String name = propertyDescriptor.getName();

            if (name.equals("class")
                    || name.equals("id")
                    || name.equals("owner")
                    || name.equals("tradePreferences")
                    || name.equals("promote")) {
                continue;
            }

            if (!targetWrap.isWritableProperty(name)) {
                continue;
            }
            Object value = sourceWrap.getPropertyValue(name);

            if (value != null) {
                targetWrap.setPropertyValue(name, value);
            }
        }
    }

    // admin panel
    public void promoteListing(UUID listingId, Boolean action, UserPrincipal currentUser) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found to promote"));

        listing.setPromote(action);
        listing.setPromotedBy(currentUser.getUsername());
        listing.setPromotedById(currentUser.getId());
        listing.setPromotedAt(LocalDateTime.now());

        listingRepository.save(listing);
    }

    public void freezeListing(UUID listingId, Boolean action, UserPrincipal currentUser) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found to promote"));

        listing.setOnHold(action);
        // listing.setPromotedBy(currentUser.getUsername());
        // listing.setPromotedById(currentUser.getId());
        // listing.setPromotedAt(LocalDateTime.now());

        listingRepository.save(listing);
    }

    public void decreaseItemQuantity(UUID listingId, int quantity) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());

        if (!listing.hasEnoughStock(quantity)) {
            throw new InsufficientStockException();
        }

        listing.setStockQuantity(listing.getStockQuantity() - quantity);

        listingRepository.save(listing);

    }

    public boolean isExistByTitle(String title) {
        return listingRepository.existsByTitle(title);
    }

    public boolean isAvailableForTrade(UUID listingId) {
        return listingRepository.isAvailableForTrade(listingId, ListingStatus.AVAILABLE);
    }

    public String getOwnerUsernameByListingId(UUID listingId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());

        return listing.getOwner().getUsername();
    }

    public List<ListingDTO> getListingDtos() {
    }

}
