package com.vinyl.VinylExchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Listing;
// import com.vinyl.VinylExchange.repository.LabelRepository;
import com.vinyl.VinylExchange.repository.ListingRepository;

@Service
public class ListingService {
    @Autowired
    private ListingRepository listingRepository;

    // @Autowired
    // private LabelRepository labelRepository;

    // public void setCoverUrl(Vinyl vinyl) {
    // vinyl.setCoverUrl("https://coverartarchive.org/release/" + vinyl.getId() +
    // "/front");
    // }

    // public String saveVinyl(Vinyl vinyl) {
    // try {
    // if (!vinylRepository.existsById(vinyl.getId())) {
    // setCoverUrl(vinyl);
    // vinylRepository.save(vinyl);
    // return "Saved succesfully.";
    // } else {
    // return "Already exist.";
    // }
    // } catch (Exception e) {
    // System.out.println(e.getLocalizedMessage());
    // return "Error, cannot save";
    // }

    // }

    public void saveListing(Listing listing) {
        // if (listing == null)
        // return;

        // Listing listing = new Listing();
        // listing.setId(listing.getId());
        // listing.setTitle(listing.getTitle());
        // listing.setDate(listing.getDate());
        // listing.setCountry(listing.getCountry());
        // listing.setBarcode(listing.getBarcode());

        listingRepository.save(listing);

    }
}
