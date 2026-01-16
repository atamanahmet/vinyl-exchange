package com.vinyl.VinylExchange.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class ImageCompressionService {

    private Logger logger = LoggerFactory.getLogger(ImageCompressionService.class);

    @PostConstruct // This runs when Spring starts the service
    public void checkWebPSupport() {
        logger.info("=== Checking WebP Support ===");

        String[] writerMIMETypes = ImageIO.getWriterMIMETypes();
        logger.info("Available writer MIME types: " + Arrays.toString(writerMIMETypes));

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            logger.info("Webp writer found: " + writer.getClass().getName());
        } else {
            logger.error(" NO webp writer found");
        }

        logger.info("=== End WebP Check ===");
    }

    public List<CompressedImage> compressImages(List<MultipartFile> images) throws IOException {

        List<CompressedImage> compressedImages = new ArrayList<>();

        for (MultipartFile file : images) {

            byte[] lossyImage = compressLossy(file, 0.80f);
            byte[] losslessImage = compressLossless(file);

            String newFileName = changeExtension(file.getOriginalFilename(), ".webp");

            compressedImages.add(new CompressedImage(newFileName, lossyImage, losslessImage));

            logger.info("Image compressed: " + newFileName);
        }

        return compressedImages;
    }

    public byte[] compressLossy(MultipartFile file, Float quality) throws IOException {
        return compressImage(file, CompressionType.LOSSY, quality);
    }

    public byte[] compressLossless(MultipartFile file) throws IOException {
        return compressImage(file, CompressionType.LOSSLESS, null);
    }

    public byte[] compressImage(MultipartFile file, CompressionType compressionType, Float quality) throws IOException {

        logger.info("Starting compression: {}, type: {}, quality: {}",
                file.getOriginalFilename(), compressionType, quality);

        BufferedImage image = ImageIO.read(file.getInputStream());

        logger.info("Image loaded: {}x{}", image.getWidth(), image.getHeight());

        // if (image == null) {
        // throw new IllegalArgumentException("Invalid image file: " +
        // file.getOriginalFilename());
        // }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

        if (!writers.hasNext()) {
            throw new IllegalStateException("No webp writer found, twelvemonkeys dependency?");
        }

        // ImageWriter imageWriter = writers.next();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);) {

            boolean written = ImageIO.write(image, "webp", outputStream);

            logger.info("ImageIO.write() returned: {}", written);

            byte[] result = outputStream.toByteArray();
            logger.info("Output size: {} bytes", result.length);

            // byte[] result = outputStream.toByteArray();

            logger.info("Compression complete. Output size: {} bytes", result.length);

            return result;

        } finally {
            // imageWriter.dispose();
        }
    }

    public static String changeExtension(String filename, String ext) {

        int i = filename.lastIndexOf(".");

        String newFileName = filename.substring(0, i) + ext;

        return newFileName;
    }
}
