package com.vinyl.VinylExchange.external.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseDTO {

    private UUID id;

    private int score;

    private String title;

    private List<ArtistCredit> artistCredit;

    private String imageUrl;

    private String date;

    private String country;

    private String barcode;

    private List<LabelInfo> labelInfo;

    private Integer trackCount;

    private List<Media> media;

    private List<Tags> tags;

    public ReleaseDTO(Release release) {
        this.id = release.getId();
        this.title = release.getTitle();
        this.artistCredit = release.getArtistCredit();
        this.imageUrl = release.getImageUrl();
        this.date = release.getDate();
        this.country = release.getCountry();
        this.barcode = release.getBarcode();
        this.labelInfo = release.getLabelInfo();
        this.trackCount = release.getTrackCount();
        this.media = release.getMedia();
        this.title = release.getTitle();
    }
}
