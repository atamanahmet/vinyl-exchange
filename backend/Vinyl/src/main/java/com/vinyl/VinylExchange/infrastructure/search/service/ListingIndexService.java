package com.vinyl.VinylExchange.infrastructure.search.service;

import java.io.IOException;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinyl.VinylExchange.infrastructure.search.document.ListingDocument;
import com.vinyl.VinylExchange.listing.Listing;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingIndexService {

    private final Logger log = LoggerFactory.getLogger(ListingIndexService.class);

    private final RestHighLevelClient openSearchClient;
    private final String INDEX_NAME = "listings";
    private final ObjectMapper objectMapper;

    @Async
    public void indexListing(Listing listing) {

        try {
            ListingDocument listingDocument = convertToDocument(listing);

            String documentJson = objectMapper.writeValueAsString(listingDocument);

            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(listing.getId().toString())
                    .source(documentJson, XContentType.JSON);

            IndexResponse indexResponse = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);

            log.info("Listing indexed for id {}: result: {}", indexResponse.getId(), indexResponse.getResult());

        } catch (IOException e) {
            log.error("Listing failed to index: {}", listing.getTitle());
        }
    }

    // price will be serialize to big decimal when writing as a json
    private ListingDocument convertToDocument(Listing listing) {
        return ListingDocument.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .artistName(listing.getArtistName())
                .year(listing.getYear())
                .status(listing.getStatus().name())
                .country(listing.getCountry())
                .format(listing.getFormat())
                .labelName(listing.getLabelName())
                .barcode(listing.getBarcode())
                .ownerUsername(listing.getOwnerUsername())
                .isPromoted(listing.isPromote())
                .price(listing.getPriceKurus())
                .createdAt(listing.getCreatedAt())
                .build();
    }

}
