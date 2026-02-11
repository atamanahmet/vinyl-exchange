package com.vinyl.VinylExchange.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.dto.ListingDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingSearchQueryService {

        private final Logger log = LoggerFactory.getLogger(ListingSearchQueryService.class);

        private final ListingSearchService listingSearchService;
        private final ListingService listingService;

        /*
         * retrieve search result ids from listingSearchService
         * retreive relevant listingDTos from listing service
         * order unordered listingDTOs with map
         * return page
         */
        public Page<ListingDTO> search(
                        String query,
                        int page,
                        int size) throws IOException {

                Page<UUID> idPage = listingSearchService.searchIds(query, page, size);

                if (idPage.isEmpty()) {
                        log.info("Page empty for search results");
                        return Page.empty(idPage.getPageable());
                }

                List<UUID> orderedIds = idPage.getContent().stream()
                                .toList();

                List<ListingDTO> unorderedDtos = listingService.getListingDTOsWithIds(orderedIds);

                Map<UUID, ListingDTO> byId = unorderedDtos.stream()
                                .collect(Collectors.toMap(
                                                dto -> dto.getId(),
                                                dto -> dto,
                                                (a, b) -> a));

                List<ListingDTO> orderedDtos = orderedIds.stream()
                                .map(byId::get)
                                .filter(dto -> dto != null)
                                .toList();

                return new PageImpl<>(
                                orderedDtos,
                                PageRequest.of(page, size),
                                idPage.getTotalElements());
        }
}
