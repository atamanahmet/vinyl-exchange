package com.vinyl.VinylExchange.controller;

import java.util.List;

import com.vinyl.VinylExchange.service.MusicBrainzService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.dto.ReleaseDTO;

@RestController
@RequestMapping("/api/mb")
@RequiredArgsConstructor
public class MusicBrainzController {

        private final MusicBrainzService musicBrainzService;

        @GetMapping("/search")
        public ResponseEntity<List<ReleaseDTO>> search(
                        @RequestParam String title,
                        @RequestParam(defaultValue = "20") int limit) {

                List<ReleaseDTO> releaseDTOs = musicBrainzService.searchTitle(title);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(releaseDTOs);

        }

}
