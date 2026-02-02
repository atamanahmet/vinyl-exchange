package com.vinyl.VinylExchange.external.musicbrainz;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.external.dto.Release;
import com.vinyl.VinylExchange.external.dto.ReleaseDTO;
import com.vinyl.VinylExchange.external.dto.RootResponse;

@Service
public class MusicBrainzService {

    private final MusicBrainzClient musicBrainzClient;

    public MusicBrainzService(MusicBrainzClient musicBrainzClient) {
        this.musicBrainzClient = musicBrainzClient;
    }

    public List<ReleaseDTO> searchTitle(String title) {

        String cleanTitle = title.trim();

        String encoded = URLEncoder.encode(cleanTitle, StandardCharsets.UTF_8);

        RootResponse RootResponse = musicBrainzClient.searchTitle(encoded, 15);

        List<Release> releases = RootResponse.getReleases();

        List<Release> updatedReleases = releases.stream()
                .sorted(Comparator.comparingInt(Release::getScore).reversed())
                // .limit(5)
                .peek(release -> release
                        .setExternalCoverUrl("http://coverartarchive.org/release/" + release.getId() + "/front-250"))
                .collect(Collectors.toList());

        return convertToDTO(updatedReleases);
    }

    private List<ReleaseDTO> convertToDTO(List<Release> releases) {

        List<ReleaseDTO> releaseDTOs = releases.stream()
                .map(release -> new ReleaseDTO(release))
                .toList();

        return releaseDTOs;
    }

}
