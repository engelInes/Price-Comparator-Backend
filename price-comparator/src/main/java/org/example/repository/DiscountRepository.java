package org.example.repository;

import org.example.model.Discount;
import org.example.utils.CsvUtil;
import org.example.utils.DateUtil;
import org.example.utils.FileNameUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository that handles loading and managing Discount entries from CSV files.
 */
@Repository
public class DiscountRepository implements ItemRepository<Discount> {

    /**
     * Loads discount entries from the given file if it matches the expected discount filename pattern.
     *
     * @param filePath The path to the CSV file.
     * @return A list of Discount objects parsed from the file.
     */
    @Override
    public List<Discount> loadEntriesFromFile(String filePath) {
        List<Discount> discounts = new ArrayList<>();

        String fileName = new File(filePath).getName();
        FileNameUtil.FileInfo fileInfo = FileNameUtil.parseFileName(fileName);

        if (!fileInfo.isDiscountFile()) {
            return discounts;
        }

        LocalDate date = fileInfo.getDate();

        for (String[] row : CsvUtil.readCsv(filePath, ";")) {
            if (row.length < 9) continue;

            try {
                Discount discount = new Discount();
                discount.setProductId(row[0]);
                discount.setProductName(row[1]);
                discount.setBrand(row[2]);
                discount.setPackageQuantity(Double.parseDouble(row[3]));
                discount.setPackageUnit(row[4]);
                discount.setProductCategory(row[5]);
                discount.setStartingDate(DateUtil.parseDate(row[6]));
                discount.setEndingDate(DateUtil.parseDate(row[7]));
                discount.setPercentageOfDiscount(Double.parseDouble(row[8]));
                discounts.add(discount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return discounts;
    }

    /**
     * Loads all discount entries by scanning the default data directory for valid discount CSV files.
     *
     * @return A list of all Discount entries found.
     */
    @Override
    public List<Discount> loadAllEntries() {
        List<Discount> allDiscounts = new ArrayList<>();

        File dataDir = new File("src/main/resources/data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            return allDiscounts;
        }

        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) {
            return allDiscounts;
        }

        for (File file : files) {
            List<Discount> discountsFromFile = loadEntriesFromFile(file.getPath());
            allDiscounts.addAll(discountsFromFile);
        }

        return allDiscounts;
    }
}
