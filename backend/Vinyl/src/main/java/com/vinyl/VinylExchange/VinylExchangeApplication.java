package com.vinyl.VinylExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

@SpringBootApplication
@EnableAsync
public class VinylExchangeApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		// Set environment variables as system properties
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

		// cloudinary test

		// Dotenv dotenv2 = Dotenv.load();
		// Cloudinary cloudinary = new Cloudinary(dotenv2.get("CLOUDINARY_URL"));
		// System.out.println(cloudinary.config.cloudName);

		// Map params1 = ObjectUtils.asMap(
		// "use_filename", true,
		// "unique_filename", false,
		// "overwrite", true);

		// try {
		// System.out.println(
		// cloudinary.uploader().upload(
		// "https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg",
		// params1));

		// Map params2 = ObjectUtils.asMap(
		// "quality_analysis", true);
		// System.out.println(
		// cloudinary.api().resource("coffee_cup", params2));
		// } catch (Exception e) {
		// System.out.println("error while cloudinary upload: " + e.getMessage());
		// }

		// Get the asset details

		SpringApplication.run(VinylExchangeApplication.class, args);
	}

}
