package com.vinyl.VinylExchange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Vinyl;
import com.vinyl.VinylExchange.domain.pojo.Label;
import com.vinyl.VinylExchange.domain.pojo.Release;
import com.vinyl.VinylExchange.domain.pojo.RootResponse;
import com.vinyl.VinylExchange.repository.LabelRepository;
import com.vinyl.VinylExchange.repository.VinylRepository;

@Service
public class VinylService {
    @Autowired
    private VinylRepository vinylRepository;

    @Autowired
    private LabelRepository labelRepository;

    // public void setCoverUrl(Vinyl vinyl) {
    // vinyl.setCoverUrl("https://coverartarchive.org/release/" + vinyl.getId() +
    // "/front");
    // }

    // public String saveVinyl(Vinyl vinyl) {
    // try {
    // if (!vinylRepository.existsById(vinyl.getId())) {
    // setCoverUrl(vinyl);
    // vinylRepository.save(vinyl);
    // return "Saved succesfully.";
    // } else {
    // return "Already exist.";
    // }
    // } catch (Exception e) {
    // System.out.println(e.getLocalizedMessage());
    // return "Error, cannot save";
    // }

    // }
    public List<Vinyl> getAllVinyl() {
        return vinylRepository.findAll();
    }

    public Optional<Vinyl> findById(String id) {
        return vinylRepository.findById(id);
    }

    public void saveReleases(RootResponse response) {
        if (response == null || response.getReleases() == null)
            return;

        for (Release release : response.getReleases()) {
            Vinyl vinyl = new Vinyl();
            vinyl.setId(release.getId());
            vinyl.setTitle(release.getTitle());
            vinyl.setDate(release.getDate());
            vinyl.setCountry(release.getCountry());
            vinyl.setBarcode(release.getBarcode());

            // media information
            if (release.getMedia() != null && !release.getMedia().isEmpty()) {
                vinyl.setFormat(release.getMedia().get(0).getFormat());
                vinyl.setTrackCount(release.getMedia().get(0).getTrackCount());
            }

            // label information - null check
            if (release.getLabelInfo() != null && !release.getLabelInfo().isEmpty()) {
                var labelInfo = release.getLabelInfo().get(0);

                if (labelInfo.getLabel() != null) {
                    Label label = labelRepository.findById(labelInfo.getLabel().getId())
                            .orElseGet(() -> {
                                Label newLabel = new Label();
                                newLabel.setId(labelInfo.getLabel().getId());
                                newLabel.setName(labelInfo.getLabel().getName());
                                return labelRepository.save(newLabel);
                            });
                    vinyl.setLabel(label);
                }
            }
            if (release.getRating() != null && !release.getLabelInfo().isEmpty()) {
                var labelInfo = release.getLabelInfo().get(0);

                if (labelInfo.getLabel() != null) {
                    Label label = labelRepository.findById(labelInfo.getLabel().getId())
                            .orElseGet(() -> {
                                Label newLabel = new Label();
                                newLabel.setId(labelInfo.getLabel().getId());
                                newLabel.setName(labelInfo.getLabel().getName());
                                return labelRepository.save(newLabel);
                            });
                    vinyl.setLabel(label);
                }
            }
            // artist credit
            if (release.getArtistCredit() != null && !release.getArtistCredit().isEmpty()) {
                var artistCredit = release.getArtistCredit().get(0);

                if (artistCredit != null) {
                    vinyl.setArtistName(artistCredit.getArtist().getName());
                    vinyl.setArtistId(artistCredit.getArtist().getId());
                }
            }

            // packaging
            if (release.getPackaging() != null) {
                vinyl.setPackaging(release.getPackaging());
            } else if (release.getMedia() != null && !release.getMedia().isEmpty()) {
                vinyl.setPackaging(release.getMedia().get(0).getFormat());
            }

            vinyl.setCoverUrl("https://coverartarchive.org/release/" + vinyl.getId() + "/front");

            vinylRepository.save(vinyl);
        }
    }
}
