package org.example.repository;

import org.example.model.PriceEntry;
import org.example.utils.CsvUtil;
import org.example.utils.FileNameUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Repository for loading and parsing product price entries from CSV files.
 */
@Repository
public class ProductRepository implements ItemRepository<PriceEntry> {

    /**
     * Loads price entries from a specific CSV file, if it's not a discount file.
     *
     * @param filePath The path to the file.
     * @return A list of PriceEntry objects.
     */
    @Override
    public List<PriceEntry> loadEntriesFromFile(String filePath) {
        List<PriceEntry> priceEntries = new ArrayList<>();

        String fileName = new File(filePath).getName();
        FileNameUtil.FileInfo fileInfo = FileNameUtil.parseFileName(fileName);

        if (fileInfo.isDiscountFile()) {
            return priceEntries;
        }

        String storeName = fileInfo.getStoreName();
        LocalDate date = fileInfo.getDate();

        for (String[] row : CsvUtil.readCsv(filePath, ";")) {
            if (row.length < 8) continue;

            try {
                PriceEntry entry = new PriceEntry();
                entry.setProductId(row[0]);
                entry.setProductName(row[1]);
                entry.setProductCategory(row[2]);
                entry.setBrand(row[3]);
                entry.setPackageQuantity(Double.parseDouble(row[4]));
                entry.setPackageUnit(row[5]);
                entry.setPrice(Double.parseDouble(row[6]));
                entry.setCurrency(row[7]);
                entry.setStoreName(storeName);
                entry.setDate(date);
                priceEntries.add(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return priceEntries;
    }

    /**
     * Loads all price entries by scanning the data directory recursively,
     * excluding discount files.
     *
     * @return A list of all PriceEntry records found.
     */
    @Override
    public List<PriceEntry> loadAllEntries() {
        List<PriceEntry> all = new ArrayList<>();
        try {
            Files.walk(Paths.get("src/main/resources/data"))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().matches(".*_\\d{4}-\\d{2}-\\d{2}\\.csv"))
                    .filter(p -> !p.getFileName().toString().contains("_discounts_"))
                    .forEach(p -> all.addAll(loadEntriesFromFile(p.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return all;
    }

    /**
     * Finds all price entries by product name.
     *
     * @param productName The product name.
     * @return List of matching price entries.
     */
    public List<PriceEntry> findByProductName(String productName) {
        return loadAllEntries().stream()
                .filter(p -> p.getProductName().equals(productName))
                .toList();
    }

    /**
     * Finds price entries by product name and store name.
     *
     * @param productName The product name.
     * @param storeName The store name.
     * @return List of matching price entries.
     */
    public List<PriceEntry> findByProductNameAndStore(String productName, String storeName) {
        return loadAllEntries().stream()
                .filter(p -> p.getProductName().equals(productName) && p.getStoreName().equals(storeName))
                .toList();
    }

    /**
     * Finds price entries by product category.
     *
     * @param category The product category.
     * @return List of matching price entries.
     */
    public List<PriceEntry> findByProductCategory(String category) {
        return loadAllEntries().stream()
                .filter(p -> p.getProductCategory().equals(category))
                .toList();
    }

    /**
     * Finds price entries by brand name.
     *
     * @param brand The brand name.
     * @return List of matching price entries.
     */
    public List<PriceEntry> findByBrand(String brand) {
        return loadAllEntries().stream()
                .filter(p -> p.getBrand().equals(brand))
                .toList();
    }
}
