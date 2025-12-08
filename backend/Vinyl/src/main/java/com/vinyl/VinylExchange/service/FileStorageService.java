package com.vinyl.VinylExchange.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    public List<String> saveImages(List<MultipartFile> images, UUID listingId) throws IOException {
        if (images == null) {
            return null;
        }
        List<String> savedPaths = new ArrayList<>();

        Path listingFolder = Paths.get(UPLOAD_DIR).resolve(listingId.toString()).toAbsolutePath();

        Files.createDirectories(listingFolder);

        try {
            for (MultipartFile file : images) {
                if (file.isEmpty())
                    continue;

                String fileName = file.getOriginalFilename();

                Path filePath = listingFolder.resolve(fileName);

                System.out.println("Saving file to: " + filePath.toAbsolutePath());

                try (InputStream inputStream = file.getInputStream()) {

                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                    String relativePath = UPLOAD_DIR + listingId + "/" + fileName;

                    savedPaths.add(relativePath); // relative path

                } catch (IOException e) {
                    System.err.println("Failed to save file: " + file.getOriginalFilename());
                    e.printStackTrace();
                }
            }

            return savedPaths;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("error while save");
        }
        return null;
    }
}
