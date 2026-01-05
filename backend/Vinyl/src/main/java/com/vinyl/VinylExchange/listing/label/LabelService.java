package com.vinyl.VinylExchange.listing.label;
// package com.vinyl.VinylExchange.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.vinyl.VinylExchange.domain.entity.Vinyl;
// import com.vinyl.VinylExchange.domain.pojo.Label;
// import com.vinyl.VinylExchange.domain.pojo.Release;
// import com.vinyl.VinylExchange.domain.pojo.RootResponse;
// import com.vinyl.VinylExchange.repository.LabelRepository;
// import com.vinyl.VinylExchange.repository.VinylRepository;

// // @Service
// public class LabelService {
// @Autowired
// private LabelRepository labelRepository;

// public void setCoverUrl(Vinyl vinyl) {
// vinyl.setCoverUrl("https://coverartarchive.org/release/" + vinyl.getId() +
// "/front");
// }

// public String saveVinyl(Vinyl vinyl) {
// try {
// if (!labelRepository.existsById(vinyl.getId())) {
// setCoverUrl(vinyl);
// labelRepository.save(vinyl);
// return "Saved succesfully.";
// } else {
// return "Already exist.";
// }
// } catch (Exception e) {
// System.out.println(e.getLocalizedMessage());
// return "Error, cannot save";
// }

// }

// public void saveReleases(RootResponse response) {
// if (response == null || response.getReleases() == null)
// return;

// for (Release release : response.getReleases()) {
// Vinyl vinyl = new Vinyl();
// vinyl.setId(release.getId());
// vinyl.setTitle(release.getTitle());
// // vinyl.setStatus(release.getStatus());
// vinyl.setDate(release.getDate());
// vinyl.setCountry(release.getCountry());
// // vinyl.setBarcode(release.getBarcode());
// if (release.getMedia() != null && !release.getMedia().isEmpty()) {
// vinyl.setFormat(release.getMedia().get(0).getFormat());
// vinyl.setTrackCount(release.getMedia().get(0).getTrackCount());
// vinyl.setPackaging(release.getMedia().get(0).getFormat());
// }

// // Label
// if (release.getLabelInfo() != null && !release.getLabelInfo().isEmpty()) {
// var labelInfo = release.getLabelInfo().get(0);
// Label label = labelRepository.findById(labelInfo.getLabel().getId())
// .orElseGet(() -> {
// Label newLabel = new Label();
// newLabel.setId(labelInfo.getLabel().getId());
// newLabel.setName(labelInfo.getLabel().getName());
// return labelRepository.save(newLabel);
// });
// vinyl.setLabel(label);
// }

// // Packaging
// // if (release.getPackaging() != null) {
// // vinyl.setPackaging(release.getPackaging());
// // } else if (release.getMedia() != null && !release.getMedia().isEmpty()) {
// // vinyl.setPackaging(release.getMedia().get(0).getFormat());
// // }

// // Format (CD, Vinyl, etc.)
// // if (release.getMedia() != null && !release.getMedia().isEmpty()) {
// // vinyl.setFormat(release.getMedia().get(0).getFormat());
// // vinyl.setTrackCount(release.getMedia().get(0).getTrackCount());
// // }

// // Optional: cover URL if you have it in your RootResponse
// // vinyl.setCoverUrl(release.getCoverUrl());

// // Save to DB
// vinylRepository.save(vinyl);
// }
// }
// }
