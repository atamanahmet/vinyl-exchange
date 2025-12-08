package com.vinyl.VinylExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vinyl.VinylExchange.domain.dto.ListingDTO;
import com.vinyl.VinylExchange.domain.dto.TradePreferenceDTO;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.TradePreference;
import com.vinyl.VinylExchange.domain.entity.User;

import com.vinyl.VinylExchange.repository.ListingRepository;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private FileStorageService fileStorageService;

    public ListingService(ListingRepository listingRepository, FileStorageService fileStorageService) {
        this.listingRepository = listingRepository;
        this.fileStorageService = fileStorageService;
    }

    public Listing saveListing(Listing listing) {
        return listingRepository.save(listing);

    }

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

            Listing savedListing = saveListing(listing);

            List<String> filePaths = fileStorageService.saveImages(
                    images,
                    savedListing.getId());

            savedListing.setImagePaths(filePaths);

            saveListing(savedListing);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
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
}
