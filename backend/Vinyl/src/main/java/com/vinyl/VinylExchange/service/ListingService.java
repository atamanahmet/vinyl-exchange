package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.repository.ListingRepository;

@Service
public class ListingService {
    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing saveListing(Listing listing) {
        return listingRepository.save(listing);

    }

    public List<Listing> getAllListingsByUserId(UUID ownerId) {
        return listingRepository.findAllByOwner_Id(ownerId);
    }
}
