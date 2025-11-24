package com.vinyl.VinylExchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinyl.VinylExchange.domain.pojo.RootResponse;
import com.vinyl.VinylExchange.service.VinylService;

@Controller
public class MainController {
        @Autowired
        private VinylService vinylService;

        // String artist = "";
        // String url = "https://musicbrainz.org/ws/2/release?query=artist:" + artist +
        // "&fmt=json";

        @GetMapping("/")
        public ResponseEntity<String> mainPage() throws JsonProcessingException {
                String searchString = "david_bowie";
                System.out.println("Logged");
                // Scanner scan = new Scanner(System.in);

                // System.out.println("enter text");
                // String text = scan.nextLine();
                try {
                        WebClient client = WebClient.builder()
                                        .baseUrl("https://musicbrainz.org/ws/2/release")
                                        .defaultHeader("User-Agent", "testApp/0.1 (foxitrot42@gmail.com)")
                                        .build();

                        RootResponse result = client.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .queryParam("query", "artist:david_bowie")
                                                        .queryParam("fmt", "json")
                                                        // .queryParam("limit", 10)
                                                        .build())
                                        .retrieve()
                                        .bodyToMono(RootResponse.class).block();

                        System.out.println(result);
                        ObjectMapper mapper = new ObjectMapper();

                        vinylService.saveReleases(result);

                        String res = mapper.writeValueAsString(result);

                        return new ResponseEntity<>(res, HttpStatus.OK);
                } catch (Exception e) {
                        return new ResponseEntity<>("Wait for refresh", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                }
        }
}
