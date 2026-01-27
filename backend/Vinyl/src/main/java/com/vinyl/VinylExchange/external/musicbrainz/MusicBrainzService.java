package com.vinyl.VinylExchange.external.musicbrainz;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.external.dto.Release;
import com.vinyl.VinylExchange.external.dto.RootResponse;

@Service
public class MusicBrainzService {

    private final MusicBrainzClient musicBrainzClient;

    public MusicBrainzService(MusicBrainzClient musicBrainzClient) {
        this.musicBrainzClient = musicBrainzClient;
    }

    public RootResponse searchTitle(String title) {

        String cleanTitle = title.trim();

        String encoded = URLEncoder.encode(cleanTitle, StandardCharsets.UTF_8);

        RootResponse RootResponse = musicBrainzClient.searchTitle(encoded, 15);

        List<Release> releases = RootResponse.getReleases();

        for (Release release : releases) {
            release.setImagePaths("http://coverartarchive.org/release/" + release.getId() + "/front-250");
            System.out.println(release.getImagePaths());
        }

        RootResponse newRoot = new RootResponse(releases);

        return newRoot;
    }

}
