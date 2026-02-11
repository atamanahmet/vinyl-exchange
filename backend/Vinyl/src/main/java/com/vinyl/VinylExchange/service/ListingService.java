package com.vinyl.VinylExchange.service;

import java.beans.PropertyDescriptor;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vinyl.VinylExchange.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.enums.ListingStatus;
import com.vinyl.VinylExchange.event.ListingCreatedEvent;
import com.vinyl.VinylExchange.event.ListingUpdatedEvent;
import com.vinyl.VinylExchange.dto.TradePreferenceDTO;
import com.vinyl.VinylExchange.exception.InsufficientStockException;
import com.vinyl.VinylExchange.exception.ListingNotFoundException;
import com.vinyl.VinylExchange.exception.UnauthorizedActionException;

import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.principal.UserDetailsImpl;

import com.vinyl.VinylExchange.infrastructure.ImageSource;
import com.vinyl.VinylExchange.domain.entity.TradePreference;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ListingService {

    private final Logger logger = LoggerFactory.getLogger(ListingService.class);

    private final ListingRepository listingRepository;
    private final FileStorageService fileStorageService;
    private final CartService cartService;
    private final CoverArtService coverArtService;

    // publish event to wishlist check -> send notification
    private final ApplicationEventPublisher eventPublisher;

    public ListingService(
            ListingRepository listingRepository,
            FileStorageService fileStorageService,
            @Lazy CartService cartService,
            WebClient webClient,
            CoverArtService coverArtService,
            ApplicationEventPublisher eventPublisher) {

        this.listingRepository = listingRepository;
        this.fileStorageService = fileStorageService;
        this.cartService = cartService;
        this.coverArtService = coverArtService;
        this.eventPublisher = eventPublisher;
    }

    public List<Listing> getAllListings() {

        return listingRepository.findAll();
    }

    public Page<Listing> getAllListingsPageable(Pageable pageable) {

        return listingRepository.findAll(pageable);
    }

    public Page<ListingDTO> getAllAvailableListings(Pageable pageable) {

        Page<Listing> listingsPage = listingRepository.findAllWithStatus(ListingStatus.AVAILABLE, pageable);

        Page<ListingDTO> listingDTOsPage = listingsPage.map(listing -> converToDTO(listing));

        return listingDTOsPage;
    }

    public Page<ListingDTO> getPublicListings(Pageable pageable) {

        Page<Listing> listingsPage = listingRepository.findAllWithStatus(ListingStatus.AVAILABLE, pageable);

        Page<ListingDTO> listingDTOsPage = listingsPage.map(listing -> converToDTO(listing));

        return listingDTOsPage;
    }

    public List<Listing> getPromotedListings() {

        return listingRepository.findByPromoteTrue();
    }

    public List<ListingDTO> getFilteredPromotedListingDTOs(UUID userId) {

        Set<UUID> cartListingIds = cartService
                .getCartDTO(userId)
                .getItems()
                .stream()
                .map(item -> item.getListingId()).collect(Collectors.toSet());

        if (cartListingIds.isEmpty()) {
            return List.of();
        }

        List<Listing> promotedListings = listingRepository.findByPromoteTrue();

        // only first 3 item to fit in cartPage
        List<Listing> filteredPromotedList = promotedListings.stream()
                .filter(listing -> !cartListingIds.contains(listing.getId()))
                .limit(5)
                .collect(Collectors.toList());

        return filteredPromotedList.stream()
                .map(filteredItem -> converToDTO(filteredItem))
                .toList();
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

    public List<ListingDTO> getListingDTOsWithIds(List<UUID> listingIdList) {

        List<Listing> resultListings = listingRepository.findAllByIdIn(listingIdList);

        List<ListingDTO> listingDTOs = new ArrayList<>();

        for (Listing listing : resultListings) {
            List<TradePreferenceDTO> tradePreferenceDTOs = new ArrayList<>();

            for (TradePreference tradePreference : listing.getTradePreferences()) {

                tradePreferenceDTOs.add(new TradePreferenceDTO(tradePreference));
            }

            List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

            if (imagePaths == null || imagePaths.isEmpty()) {
                imagePaths = fileStorageService.getListingImagePaths(listing.getMbId());
            }

            listingDTOs.add(new ListingDTO(listing, imagePaths));
        }

        return listingDTOs;
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

            if (imagePaths == null || imagePaths.isEmpty()) {
                imagePaths = fileStorageService.getListingImagePaths(listing.getMbId());
            }

            listingDTOs.add(new ListingDTO(listing, imagePaths));
        }

        return listingDTOs;
    }

    public Listing saveListing(Listing listing) {

        return listingRepository.save(listing);
    }

    // Clean up files also, DDD loose couple, holding only Id
    public void deleteListing(UUID listingId, UserDetailsImpl userPrincipal) {

        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new ListingNotFoundException());

        if (listing.getOwner().getId().equals(userPrincipal.getId())) {

            fileStorageService.deleteListingImages(listingId);

            listingRepository.deleteById(listingId);
        }
    }

    public Listing findListingById(UUID listingId) {

        return listingRepository
                .findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());
    }

    public long totalCount() {
        return listingRepository.count();
    }

    // TODO: refactor
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

            if (images != null && !images.isEmpty()) {
                logger.info("Uploaded images: {}", images.size());

                List<ImageSource> imageSources = getImageSourceList(images);
                fileStorageService.saveImages(imageSources, savedListing.getId());

            }

            if (listing.getMbId() != null) {
                coverArtService.fetchAndSaveCoverAsync(
                        savedListing.getId(),
                        listing.getMbId());
            }

            // redundant
            savedListing = listingRepository.save(savedListing);

            eventPublisher.publishEvent(
                    ListingCreatedEvent.builder()
                            .listing(savedListing).build());

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // for exception catch, constructor wont catch
    public ImageSource convertToImageSource(MultipartFile image) {
        try {
            return new ImageSource(
                    image.getInputStream(),
                    image.getOriginalFilename(),
                    image.getContentType(),
                    image.getSize());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read uploaded image to ImageSource: " + e.getMessage());
        }
    }

    public List<ImageSource> getImageSourceList(List<MultipartFile> images) {
        List<ImageSource> imageSources = new ArrayList<>();

        for (MultipartFile image : images) {
            imageSources.add(
                    convertToImageSource(image));
        }

        return imageSources;
    }

    public void updateListing(UUID id, String listingJson, List<MultipartFile> images, UUID userId) {

        Listing existingListing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException());

        // Security check
        if (!existingListing.getOwner().getId().equals(userId)) {
            throw new UnauthorizedActionException("Listing update unauthorized, action caused by userId: " + userId);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ListingDTO listingDTO = objectMapper.readValue(listingJson, ListingDTO.class);

            handleImageUpdates(id, listingDTO.getImagePaths(), images);

            Listing updatedListing = new Listing();
            updatedListing.setTitle(listingDTO.getTitle());
            updatedListing.setArtistName(listingDTO.getArtistName());
            updatedListing.setDescription(listingDTO.getDescription());
            updatedListing.setPriceKurus(listingDTO.getPriceKurus());
            updatedListing.setDiscountBP(listingDTO.getDiscountBP());
            updatedListing.setTradeable(listingDTO.getTradeable());
            updatedListing.setTradeValue(listingDTO.getTradeValue());
            updatedListing.setCondition(listingDTO.getCondition());
            updatedListing.setFormat(listingDTO.getFormat());
            updatedListing.setPackaging(listingDTO.getPackaging());
            updatedListing.setYear(listingDTO.getYear());
            updatedListing.setCountry(listingDTO.getCountry());
            updatedListing.setLabelName(listingDTO.getLabelName());
            updatedListing.setBarcode(listingDTO.getBarcode());
            updatedListing.setTrackCount(listingDTO.getTrackCount());

            copyNonNullFields(updatedListing, existingListing);

            updateTradePreferences(existingListing, listingDTO.getTradePreferences());

            Listing savedListing = saveListing(existingListing);

            eventPublisher.publishEvent(
                    ListingUpdatedEvent.builder()
                            .listing(savedListing));

        } catch (Exception e) {
            System.out.println("Error updating listing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /// gets old images, check deleted, add new images
    private void handleImageUpdates(UUID listingId, List<String> keptImageUrls, List<MultipartFile> newImages) {

        try {

            List<String> currentImageUrls = fileStorageService.getListingImagePaths(listingId);

            List<String> imagesToDelete = currentImageUrls.stream()
                    .filter(url -> keptImageUrls == null || !keptImageUrls.contains(url))
                    .toList();

            for (String urlToDelete : imagesToDelete) {
                String filename = urlToDelete.substring(urlToDelete.lastIndexOf("/") + 1);
                try {
                    fileStorageService.deleteImage(listingId, filename);
                } catch (Exception e) {
                    System.out.println("Failed to delete image: " + filename);
                }
            }

            if (newImages != null && !newImages.isEmpty()) {
                List<ImageSource> imageSources = getImageSourceList(newImages);
                fileStorageService.saveImages(imageSources, listingId);
            }
        } catch (Exception e) {
            System.out.println("Error handling image updates: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // clear old, put new
    private void updateTradePreferences(Listing existingListing, List<TradePreferenceDTO> tradePreferenceDTOs) {
        existingListing.getTradePreferences().clear();

        if (tradePreferenceDTOs != null && !tradePreferenceDTOs.isEmpty()) {
            tradePreferenceDTOs.forEach(prefDTO -> {
                TradePreference pref = new TradePreference();
                pref.setDesiredItem(prefDTO.getDesiredItem());
                pref.setExtraAmount(prefDTO.getExtraAmount());
                pref.setPaymentDirection(prefDTO.getPaymentDirection());
                pref.setListing(existingListing);
                existingListing.getTradePreferences().add(pref);
            });
        }
    }

    public List<ListingDTO> getUserListingsWithStatus(UUID ownerId, ListingStatus status) {

        List<Listing> userListings = listingRepository.findAllByOwner_IdAndStatus(ownerId, status);

        if (userListings == null || userListings.isEmpty())
            return List.of();

        return userListings.stream()
                .map(listing -> converToDTO(listing))
                .toList();
    }

    public ListingDTO getListingDTOById(UUID listingId) {

        Listing listing = listingRepository
                .findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException());

        return converToDTO(listing);
    }

    // TODO: refactor
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

    // TODO: remove, admin panel
    public void promoteListing(UUID listingId, Boolean action, UserDetailsImpl currentUser) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found to promote"));

        listing.setPromote(action);
        listing.setPromotedBy(currentUser.getUsername());
        listing.setPromotedById(currentUser.getId());
        listing.setPromotedAt(LocalDateTime.now());

        listingRepository.save(listing);
    }

    public void freezeListing(UUID listingId, Boolean action, UserDetailsImpl currentUser) {

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

    public List<ListingDTO> getAllListingDTOs() {

        List<Listing> listings = getAllListings();

        return listings.stream()
                .map(listing -> converToDTO(listing))
                .toList();
    }

    public ListingDTO converToDTO(Listing listing) {

        List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

        if ((imagePaths == null || imagePaths.isEmpty()) && listing.getMbId() != null) {
            imagePaths = fileStorageService.getListingImagePaths(listing.getMbId());
        }

        return new ListingDTO(listing, imagePaths);
    }

    public List<ListingDTO> converAllToDTO(List<Listing> listings) {

        List<ListingDTO> listingDTOs = new ArrayList<>();

        for (Listing listing : listings) {

            List<String> imagePaths = fileStorageService.getListingImagePaths(listing.getId());

            if (imagePaths == null || imagePaths.isEmpty()) {
                imagePaths = fileStorageService.getListingImagePaths(listing.getMbId());
            }

            listingDTOs.add(new ListingDTO(listing, imagePaths));
        }

        return listingDTOs;
    }

    public void restoreStock(UUID listingId, int quantity) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not foujnd for restock"));

        listing.setStockQuantity(listing.getStockQuantity() + quantity);

        listingRepository.save(listing);

    }

}
