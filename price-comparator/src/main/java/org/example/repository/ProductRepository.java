package org.example.repository;

import org.apache.tomcat.jni.FileInfo;
import org.example.model.PriceEntry;
import org.example.utils.CsvUtil;
import org.example.utils.FileNameUtil;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductRepository implements ItemRepository<PriceEntry> {

    private static final Pattern DATE_PATTERN = Pattern.compile(".*_(\\d{4}-\\d{2}-\\d{2})\\.csv$");
    @Override
    public List<PriceEntry> loadEntriesFromFile(String filePath) {
        List<PriceEntry> priceEntries = new ArrayList<>();

        String fileName = new File(filePath).getName();
        FileNameUtil.FileInfo fileInfo = FileNameUtil.parseFileName(fileName);

        if (fileInfo.isDiscountFile()) {
            System.out.println("Skipping discount files: " + fileName);
            return priceEntries;
        }

        String storeName = fileInfo.getStoreName();
        LocalDate date = fileInfo.getDate();

        for (String[] row : CsvUtil.readCsv(filePath, ";")) {
            if (row.length < 8) continue;

            try {
                PriceEntry entry = new PriceEntry();
                entry.setProductId(row[0]);
                entry.setStoreName(storeName);
                entry.setDate(date);
                entry.setPrice(Double.parseDouble(row[6]));
                priceEntries.add(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return priceEntries;
    }
}
