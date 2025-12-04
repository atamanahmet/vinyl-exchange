package com.vinyl.VinylExchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinyl.VinylExchange.domain.entity.Vinyl;
import com.vinyl.VinylExchange.domain.pojo.Release;
import com.vinyl.VinylExchange.domain.pojo.RootResponse;
import com.vinyl.VinylExchange.service.VinylService;

import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
        @Autowired
        private VinylService vinylService;

        boolean connect = false;

        @GetMapping("/")
        public ResponseEntity<String> mainPage() throws JsonProcessingException {
                System.out.println("Logged");
                ObjectMapper mapper = new ObjectMapper();
                String response;

                try {
                        if (connect) {
                                WebClient client = WebClient.builder()
                                                .baseUrl("https://musicbrainz.org/ws/2/release")
                                                .defaultHeader("User-Agent", "testApp/0.1 (foxitrot42@gmail.com)")
                                                .build();

                                RootResponse result = client.get()
                                                .uri(uriBuilder -> uriBuilder
                                                                .queryParam("query", "artist:david_bowie")
                                                                .queryParam("fmt", "json")
                                                                .queryParam("inc", "ratings")
                                                                .queryParam("limit", 100)
                                                                .build())
                                                .retrieve()
                                                .bodyToMono(RootResponse.class)
                                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                                                                .maxBackoff(Duration.ofSeconds(10))
                                                                .filter(throwable -> throwable instanceof WebClientResponseException
                                                                                || throwable.getMessage().contains(
                                                                                                "Connection reset")))
                                                .timeout(Duration.ofSeconds(30)) // Add timeout
                                                .block();

                                // System.out.println(result);

                                if (result != null) {
                                        // rate limit for mb api
                                        Thread.sleep(1000);

                                        vinylService.saveReleases(result);
                                } else {
                                        return new ResponseEntity<>("No data received from API",
                                                        HttpStatus.NO_CONTENT);
                                }
                        }
                        response = mapper.writeValueAsString(vinylService.getAllVinyl());

                        return new ResponseEntity<>(response, HttpStatus.OK);

                } catch (Exception e) {
                        System.out.println("Connection Error" + e.getLocalizedMessage());
                        return new ResponseEntity<>("Wait for refresh", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                }
        }

        @GetMapping("/search/{query}")
        public ResponseEntity<String> search(@PathVariable(name = "query", required = true) String query) {
                System.out.println("search triggered");
                String response;
                ObjectMapper mapper = new ObjectMapper();
                RootResponse result;

                try {
                        WebClient client = WebClient.builder()
                                        .baseUrl("https://musicbrainz.org/ws/2/release")
                                        .defaultHeader("User-Agent", "testApp/0.1 (foxitrot42@gmail.com)")
                                        .build();

                        result = client.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .queryParam("query", "release:" + query)
                                                        .queryParam("fmt", "json")
                                                        .queryParam("inc", "ratings")
                                                        .queryParam("limit", 100)
                                                        .build())
                                        .retrieve()
                                        .bodyToMono(RootResponse.class)
                                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                                                        .maxBackoff(Duration.ofSeconds(10))
                                                        .filter(throwable -> throwable instanceof WebClientResponseException
                                                                        || throwable.getMessage().contains(
                                                                                        "Connection reset")))
                                        .timeout(Duration.ofSeconds(30)) // add timeout
                                        .block();

                        // System.out.println(result);

                        if (result != null) {
                                // rate limit for mb api
                                Thread.sleep(1000);

                                vinylService.saveReleases(result);
                        } else {
                                return new ResponseEntity<>("No data received from API",
                                                HttpStatus.NO_CONTENT);
                        }
                        List<Vinyl> list = new ArrayList<>();
                        for (Release item : result.getReleases()) {
                                list.add(vinylService.findById(item.getId()).get());
                        }
                        response = mapper.writeValueAsString(list);

                        return new ResponseEntity<>(response, HttpStatus.OK);
                }

                catch (Exception e) {
                        System.out.println("Connection Error" + e.getLocalizedMessage());
                        return new ResponseEntity<>("Wait for refresh", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                }
        }
}
