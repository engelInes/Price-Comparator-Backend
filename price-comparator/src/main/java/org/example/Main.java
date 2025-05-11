package org.example;

import org.example.model.Discount;
import org.example.model.PriceEntry;
import org.example.repository.DiscountRepository;
import org.example.repository.ProductRepository;
import org.example.utils.FileNameUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ProductRepository productRepo = new ProductRepository();
        DiscountRepository discountRepo = new DiscountRepository();

        String folderPath = "data";

        ClassLoader classLoader = getClass().getClassLoader();
        java.net.URL resource = classLoader.getResource(folderPath);

        if (resource == null) {
            System.err.println("Could not find folder: " + folderPath);
            return;
        }

        File folder = new File(resource.toURI());
        File[] allFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (allFiles == null || allFiles.length == 0) {
            System.out.println("No CSV files found: " + folderPath);
            return;
        }
        for (File file : allFiles) {
            String fileName = file.getName();
            FileNameUtil.FileInfo fileInfo = FileNameUtil.parseFileName(fileName);

            System.out.println("Processing file: " + fileName);

            if (fileInfo.isDiscountFile()) {
                System.out.println("  - Identified as discount file");
                List<Discount> discounts = discountRepo.loadEntriesFromFile(file.getAbsolutePath());

                System.out.println("  - Found " + discounts.size() + " discount entries");
                discounts.forEach(discount ->
                        System.out.println("    * " + discount.getProductName() +
                                " - " + discount.getPercentageOfDiscount() + "% discount"));
            } else {
                System.out.println("  - Identified as product price file");
                List<PriceEntry> priceEntries = productRepo.loadEntriesFromFile(file.getAbsolutePath());

                System.out.println("  - Found " + priceEntries.size() + " price entries");
                priceEntries.forEach(entry ->
                        System.out.println("    * Product ID: " + entry.getProductId() +
                                " - Price: " + entry.getPrice()));
            }

            System.out.println();
        }
    }
}