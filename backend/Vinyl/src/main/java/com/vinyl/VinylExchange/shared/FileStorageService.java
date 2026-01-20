package com.vinyl.VinylExchange.shared;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Value("${file.image.url-path}")
    private String IMG_URL_PATH;

    @Value("${app.base-url}")
    private String BASE_URL;

    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final ImageCompressionService imageCompressionService;

    public FileStorageService(ImageCompressionService imageCompressionService) {
        this.imageCompressionService = imageCompressionService;
    }

    // uncompress images from http upload, for saving directly
    // refactor: only compressed
    public List<String> saveImages(List<MultipartFile> images, UUID listingId) throws IOException {

        if (images == null) {
            return null;
        }

        List<CompressedImage> compressedImages = imageCompressionService.compressImages(images);

        return saveCompressedImages(compressedImages, listingId);

    }

    public List<String> saveCompressedImages(
            List<CompressedImage> compressedImages,
            UUID listingId) throws IOException {

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        List<String> savedPaths = new ArrayList<>();

        for (CompressedImage image : compressedImages) {

            Path path = listingFolder.resolve(image.getFileName());

            Files.createDirectories(listingFolder);

            try {

                Files.write(path, image.getImage());

                String fullPath = BASE_URL + IMG_URL_PATH + listingId + "/" + image.getFileName();

                savedPaths.add(fullPath);

            } catch (IOException e) {
                System.out.println("Directory creating or file write exception: " + e.getMessage());
                throw new RuntimeException("COmpressed images save failed: " + e.getMessage());
            }
        }
        return savedPaths;
    }

    public List<String> getListingImagePaths(UUID listingId) {

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        if (!Files.exists(listingFolder)) {
            logger.debug("No image folder found for listing: {}", listingId);
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.list(listingFolder)) {

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> isImageFile(path.getFileName().toString()))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .map(filename -> BASE_URL + IMG_URL_PATH + listingId + "/" + filename) // full path for ulrs to
                                                                                           // frontend only
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to read images for listing {}: {}", listingId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteImage(UUID listingId, String filename) throws IOException {
        Path imagePath = Paths.get(UPLOAD_DIR)
                .resolve(listingId.toString())
                .resolve(filename)
                .toAbsolutePath();

        if (!imagePath.startsWith(Paths.get(UPLOAD_DIR).toAbsolutePath())) {
            throw new SecurityException("Invalid file path");
        }

        Files.deleteIfExists(imagePath);
        logger.info("Deleted image {} for listing {}", filename, listingId);
    }

    public List<CompressedImage> compressImages(List<MultipartFile> images) {
        try {
            return imageCompressionService.compressImages(images);

        } catch (IOException e) {
            logger.error("Error while compressing images: " + e.getMessage());
            throw new RuntimeException("Image compression failed;");
        }
    }

    private boolean isImageFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".webp") ||
                lower.endsWith(".gif");
    }

    public void deleteListingImages(UUID listingId) {

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        if (!Files.exists(listingFolder)) {
            logger.debug("No images to delete for listing: {}", listingId);
            return;
        }

        try (Stream<Path> paths = Files.walk(listingFolder)) {
            paths.sorted(Comparator.reverseOrder()) // deepest first
                    .forEach(path -> {
                        try {
                            FileUtils.deleteDirectory(listingFolder.toFile());
                            logger.info("Force deleted all images for listing: {}", listingId);
                        } catch (IOException e) {
                            logger.warn("Failed to force delete images for listing {}: {}", listingId, e.getMessage());
                        }
                    });
            logger.info("Deleted all images for listing: {}", listingId);
        } catch (IOException e) {
            logger.error("Failed to walk directory for listing {}: {}", listingId, e.getMessage());
        }
    }

    public String getMainImagePath(UUID listingId) {

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        if (!Files.exists(listingFolder)) {
            logger.debug("No image folder found for listing: {}", listingId);
            return null;
        }

        try (Stream<Path> paths = Files.list(listingFolder)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> isImageFile(path.getFileName().toString()))
                    .sorted()
                    .findFirst() // first image only as a mainImg
                    .map(path -> BASE_URL + IMG_URL_PATH + listingId + "/" + path.getFileName().toString())
                    .orElse(null); // null if no images found
        } catch (IOException e) {
            logger.error("read eror for main image {}: {}", listingId, e.getMessage());
            return null;
        }
    }
}
