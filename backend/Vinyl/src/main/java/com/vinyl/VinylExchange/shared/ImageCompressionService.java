package com.vinyl.VinylExchange.shared;

import java.util.ArrayList;
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

@Service
public class ImageCompressionService {

    private Logger logger = LoggerFactory.getLogger(ImageCompressionService.class);

    public List<CompressedImage> compressImages(List<MultipartFile> images) throws IOException {

        List<CompressedImage> compressedImages = new ArrayList<>();

        for (MultipartFile file : images) {

            System.out.println(file.getSize() + "byte");

            // To do: lossless new impl, webp lossless file size bigger than jpg
            // compare dpi before converst lossless

            // byte[] losslessImage = compressLossless(file);

            // smaller than 1000kb no need to compress, webp conversion reduces too much dpi
            if (file.getSize() > 100000) {
                byte[] lossyImage = compressLossy(file, 0.80f);

                String newFileName = changeExtension(file.getOriginalFilename(), ".webp");

                compressedImages.add(new CompressedImage(newFileName, lossyImage));

                logger.info("Image compressed: " + newFileName);

            } else {

                byte[] image = file.getBytes();

                compressedImages.add(new CompressedImage(file.getOriginalFilename(), image));
            }
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

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IllegalArgumentException("Invalid image file: " +
                    file.getOriginalFilename());
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");

        if (!writers.hasNext()) {
            throw new IllegalStateException("No WebP writer found");
        }

        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);) {

            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            // System.out.println("can write comppresed " +
            // writeParam.canWriteCompressed());

            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(compressionType.toString());

            if (compressionType.equals(CompressionType.LOSSY)) {
                writeParam.setCompressionQuality(0.85f);
            }

            writer.setOutput(imageOutputStream);

            writer.write(null, new IIOImage(image, null, null), writeParam);

            imageOutputStream.flush();

            // System.out.println("output size after write: " + outputStream.size());

            return outputStream.toByteArray();
        }

    }

    public static String changeExtension(String filename, String ext) {

        int i = filename.lastIndexOf(".");

        String newFileName = filename.substring(0, i) + ext;

        return newFileName;
    }
}
