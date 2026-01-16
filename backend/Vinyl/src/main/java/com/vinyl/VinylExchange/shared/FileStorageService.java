package com.vinyl.VinylExchange.shared;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final ImageCompressionService imageCompressionService;

    public FileStorageService(ImageCompressionService imageCompressionService) {
        this.imageCompressionService = imageCompressionService;
    }

    // uncompress image from http upload, for saving directly
    // refactor: only compressed
    public List<String> saveImages(List<MultipartFile> images, UUID listingId) throws IOException {
        if (images == null) {
            return null;
        }

        List<CompressedImage> compressedImages = imageCompressionService.compressImages(images);

        return saveCompressedImages(compressedImages, listingId);

        // List<String> savedPaths = new ArrayList<>();

        // Path listingFolder =
        // Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        // Files.createDirectories(listingFolder);

        // try {
        // for (MultipartFile file : images) {
        // if (file.isEmpty())
        // continue;

        // String fileName = file.getOriginalFilename();

        // Path filePath = listingFolder.resolve(fileName);

        // try (InputStream inputStream = file.getInputStream()) {

        // Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        // String relativePath = UPLOAD_DIR + listingId + "/" + fileName;

        // savedPaths.add(relativePath); // relative path

        // } catch (IOException e) {
        // System.err.println("Failed to save file: " + file.getOriginalFilename());
        // e.printStackTrace();
        // }
        // }

        // return savedPaths;
        // } catch (Exception e) {
        // // TODO: handle exception
        // System.out.println(e.getMessage());
        // e.printStackTrace();
        // System.out.println("error while save");
        // }
        // return null;
    }

    public List<String> saveCompressedImages(
            List<CompressedImage> compressedImages,
            UUID listingId) throws IOException {

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        List<String> savedPaths = new ArrayList<>();

        for (CompressedImage image : compressedImages) {

            String losslessFileName = "lossless_" + image.getFileName();
            String lossyFileName = "lossy_" + image.getFileName();

            Path losslessPath = listingFolder.resolve(losslessFileName);
            Path lossyPath = listingFolder.resolve(lossyFileName);

            Files.createDirectories(listingFolder);

            try {

                Files.write(lossyPath, image.getLossyImage());
                Files.write(losslessPath, image.getLosslessImage());

                String relativeLosslessFileNamePath = UPLOAD_DIR + listingId + "/" + losslessFileName;
                String relativeLossyPath = UPLOAD_DIR + listingId + "/" + lossyFileName;

                savedPaths.add(relativeLosslessFileNamePath);
                savedPaths.add(relativeLossyPath);

            } catch (IOException e) {
                System.out.println("Directory creating or file write exception: " + e.getMessage());
                throw new RuntimeException("COmpressed images save failed: " + e.getMessage());
            }

        }
        return savedPaths;
    }

    public List<CompressedImage> compressImages(List<MultipartFile> images) {
        try {
            return imageCompressionService.compressImages(images);

        } catch (IOException e) {
            logger.error("Error while compressing images: " + e.getMessage());
            throw new RuntimeException("Image compression failed;");
        }
    }
}
