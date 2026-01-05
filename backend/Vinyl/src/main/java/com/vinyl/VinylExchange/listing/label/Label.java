package com.vinyl.VinylExchange.listing.label;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.vinyl.VinylExchange.listing.Vinyl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "label")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Label {

    @Id
    private String id;
    private String name;

    @OneToMany(mappedBy = "label")
    @JsonIgnore
    private List<Vinyl> vinyls = new ArrayList<>();

}