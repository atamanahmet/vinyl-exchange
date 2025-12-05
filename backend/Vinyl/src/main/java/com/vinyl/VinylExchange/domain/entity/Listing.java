package com.vinyl.VinylExchange.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vinyl.VinylExchange.domain.pojo.Label;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String status;
    private String date;
    private String country;
    private String barcode;
    private String packaging; // size
    private String format;
    private int trackCount;

    private String artistName;
    private String artistId;

    private boolean tradeable;

    private double price;

    private double tradeValue;

    private double discount;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradePreference> tradePreferences = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "label_id")
    private Label label;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_path")
    private List<String> imagePaths = new ArrayList<>();

    // helper
    public void addTradePreference(TradePreference newTradePreference) {
        newTradePreference.setListing(this);
        tradePreferences.add(newTradePreference);
    }

    // helper
    public void removeTradePreference(TradePreference tradePreference) {
        tradePreferences.remove(tradePreference);
        tradePreference.setListing(null);
    }
}