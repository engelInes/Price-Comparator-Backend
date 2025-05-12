package org.example.utils;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing file names.
 */
public class FileNameUtil {
    private static final Pattern PRICE_FILE_PATTERN = Pattern.compile("^([^_]+)_(\\d{4}-\\d{2}-\\d{2})\\.csv$");
    private static final Pattern DISCOUNT_FILE_PATTERN = Pattern.compile("^([^_]+)_discounts_(\\d{4}-\\d{2}-\\d{2})\\.csv$");

    /**
     * Metadata extracted from a file name.
     */
    public static class FileInfo {
        private final String storeName;
        private final LocalDate date;
        private final boolean isDiscountFile;

        /**
         * Constructs a FileInfo object.
         *
         * @param storeName       The name of the store.
         * @param date            The date associated with the file.
         * @param isDiscountFile  Whether the file is a discount file.
         */
        public FileInfo(String storeName, LocalDate date, boolean isDiscountFile) {
            this.storeName = storeName;
            this.date = date;
            this.isDiscountFile = isDiscountFile;
        }

        /**
         * Returns the store name.
         *
         * @return The store name.
         */
        public String getStoreName() {
            return storeName;
        }

        /**
         * Returns the date extracted from the file name.
         *
         * @return The file date.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Indicates whether this file represents discount data.
         *
         * @return true if discount file, false otherwise.
         */
        public boolean isDiscountFile() {
            return isDiscountFile;
        }
    }

    /**
     * Parses a file name to extract metadata such as store name, date, and file type.
     *
     * @param fileName The file name to parse.
     * @return FileInfo object containing the parsed metadata.
     */
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
