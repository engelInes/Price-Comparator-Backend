package org.example.utils;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtil {
    private static final Pattern PRICE_FILE_PATTERN = Pattern.compile("^([^_]+)_(\\d{4}-\\d{2}-\\d{2})\\.csv$");
    private static final Pattern DISCOUNT_FILE_PATTERN = Pattern.compile("^([^_]+)_discounts_(\\d{4}-\\d{2}-\\d{2})\\.csv$");

    public static class FileInfo {
        private final String storeName;
        private final LocalDate date;
        private final boolean isDiscountFile;

        public FileInfo(String storeName, LocalDate date, boolean isDiscountFile) {
            this.storeName = storeName;
            this.date = date;
            this.isDiscountFile = isDiscountFile;
        }

        public String getStoreName() {
            return storeName;
        }

        public LocalDate getDate() {
            return date;
        }

        public boolean isDiscountFile() {
            return isDiscountFile;
        }
    }

    public static FileInfo parseFileName(String fileName) {
        Matcher discountMatcher = DISCOUNT_FILE_PATTERN.matcher(fileName);
        if (discountMatcher.matches()) {
            String storeName = discountMatcher.group(1);
            LocalDate date = LocalDate.parse(discountMatcher.group(2));
            return new FileInfo(storeName, date, true);
        }

        Matcher priceMatcher = PRICE_FILE_PATTERN.matcher(fileName);
        if (priceMatcher.matches()) {
            String storeName = priceMatcher.group(1);
            LocalDate date = LocalDate.parse(priceMatcher.group(2));
            return new FileInfo(storeName, date, false);
        }

        System.out.println("Unrecognized filename pattern: " + fileName);
        return new FileInfo("unknown", LocalDate.now(), false);
    }
}
