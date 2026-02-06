package com.vinyl.VinylExchange.infrastructure.search.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingSearchService {

    private static final String INDEX_NAME = "listings";

    private final RestHighLevelClient openSearchClient;

    // TODO: promote boost for search results?
    public Page<UUID> searchIds(
            String query,
            int page,
            int size) throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(buildQuery(query))
                .from(page * size)
                .size(size);

        searchRequest.source(source);

        SearchResponse response = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

        List<UUID> ids = Arrays.stream(response.getHits().getHits())
                .map(hit -> hit.getId())
                .map(id -> UUID.fromString(id))
                .toList();

        long totalHits = response.getHits().getTotalHits().value;

        return new PageImpl<>(ids, PageRequest.of(page, size), totalHits);
    }

    private QueryBuilder buildQuery(String query) {

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        if (query == null || query.isBlank()) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(query)
                            .field("title", 3.0f)
                            .field("artistName", 2.5f)
                            .field("labelName", 1.5f)
                            .field("format")
                            // .operator(Operator.AND)
                            // .fuzziness(Fuzziness.AUTO)
                            .type(MultiMatchQueryBuilder.Type.BEST_FIELDS));
        }

        // only active listings
        boolQuery.filter(
                QueryBuilders.termQuery("status.keyword", "AVAILABLE"));

        return boolQuery;
    }
}
