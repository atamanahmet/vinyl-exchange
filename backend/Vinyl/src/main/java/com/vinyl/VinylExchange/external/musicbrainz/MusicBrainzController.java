package com.vinyl.VinylExchange.external.musicbrainz;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.external.dto.ReleaseDTO;

@RestController
@RequestMapping("/api/mb")
public class MusicBrainzController {

        private final MusicBrainzService musicBrainzService;

        public MusicBrainzController(MusicBrainzService musicBrainzService) {
                this.musicBrainzService = musicBrainzService;
        }

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
