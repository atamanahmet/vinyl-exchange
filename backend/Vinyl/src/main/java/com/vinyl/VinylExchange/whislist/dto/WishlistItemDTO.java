package com.vinyl.VinylExchange.whislist.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistItemDTO {

    private Long id;

    private String title;

    private String artist;

    private Integer year;

    private String format;

    private String imageUrl;

    private LocalDateTime addedAt;

}
