package com.vinyl.VinylExchange.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vinyl.VinylExchange.domain.pojo.Label;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Vinyl extends BaseEntity {

    @Id
    private String id;

    private String title;
    private String status;
    private String date;
    private String country;
    private String barcode;
    private String packaging; // size?
    private String format;
    private int trackCount;
    private String coverUrl;
    private String artistName;
    private String artistId;

    @ManyToOne
    @JoinColumn(name = "label_id")
    private Label label;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;
}