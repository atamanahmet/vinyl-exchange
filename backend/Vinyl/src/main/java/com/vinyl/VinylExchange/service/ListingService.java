package com.vinyl.VinylExchange.service;

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

import com.vinyl.VinylExchange.domain.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.dto.TradePreferenceDTO;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.TradePreference;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.InsufficientStockException;
import com.vinyl.VinylExchange.exception.ListingNotFoundException;

import com.vinyl.VinylExchange.repository.ListingRepository;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

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

            listingDTOs.add(new ListingDTO(listing, tradePreferenceDTOs));
        }

        return listingDTOs;
    }

    public Listing saveListing(Listing listing) {

        return listingRepository.save(listing);
    }

    public void deleteListing(UUID listingId, UserPrincipal userPrincipal) {

        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException());

        if (listing.getOwner().getId().equals(userPrincipal.getId()))
            listingRepository.deleteById(listingId);
    }

    public Listing getListingById(UUID listingId) {

        return listingRepository
                .findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());
    }

    @Transactional
    public void createNewListing(
            String listingJson,
            List<MultipartFile> images,
            User user) {

        boolean edit = false;

        try {

            Listing listing = new ObjectMapper().readValue(listingJson, Listing.class);

            edit = listing.getId() != null && listingRepository.existsById(listing.getId());

            listing.setOwner(user);

            if (listing.getTradePreferences() != null) {

                listing.getTradePreferences()
                        .forEach(preference -> preference.setListing(listing));
            }

            if (edit) {

                Listing oldListing = listingRepository
                        .findById(listing.getId())
                        .orElseThrow(() -> new ListingNotFoundException());

                List<String> existingUploads = new ArrayList<>(oldListing.getImagePaths());

                List<String> newFilePaths = new ArrayList<>();
                if (images != null && !images.isEmpty()) {
                    newFilePaths = fileStorageService.saveImages(images, oldListing.getId());
                }

                existingUploads.addAll(newFilePaths);

                copyNonNullFields(listing, oldListing);

                oldListing.getTradePreferences().clear();

                if (listing.getTradePreferences() != null) {
                    for (TradePreference preference : listing.getTradePreferences()) {
                        preference.setListing(oldListing);
                        oldListing.getTradePreferences().add(preference);
                    }
                }

                oldListing.setImagePaths(existingUploads);

                saveListing(oldListing);

            } else {
                // for getting id
                Listing savedListing = saveListing(listing);

                if (images != null) {
                    List<String> newFilePaths = fileStorageService.saveImages(
                            images,
                            savedListing.getId());

                    savedListing.setImagePaths(newFilePaths);

                    saveListing(savedListing);
                }

            }

        } catch (Exception e) {
            // // TODO: handle exception
            throw new RuntimeException(e);
        }
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

            listingDTOs.add(new ListingDTO(listing, tradePreferenceDTOs));
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
        ListingDTO listingDTO = new ListingDTO(listing, tradePreferenceDTOs);

        return listingDTO;
    }

    public static void copyNonNullFields(Listing source, Listing target) {
        BeanWrapper sourceWrap = new BeanWrapperImpl(source);
        BeanWrapper targetWrap = new BeanWrapperImpl(target);

        for (PropertyDescriptor propertyDescriptor : sourceWrap.getPropertyDescriptors()) {
            String name = propertyDescriptor.getName();

            if (name.equals("class")
                    || name.equals("id")
                    || name.equals("imagePaths")
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

}
