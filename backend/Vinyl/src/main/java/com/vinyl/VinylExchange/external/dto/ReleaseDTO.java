package com.vinyl.VinylExchange.external.dto;

import java.util.List;

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

    private int score;

    private String title;

    private List<ArtistCredit> artistCredit;

    private String date;

    private String country;

    private String barcode;

    private LabelInfo labelInfo;

    private Integer trackCount;

    private Media media;

    private List<Tags> tags;
}
