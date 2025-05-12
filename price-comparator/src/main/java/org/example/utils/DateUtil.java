package org.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for parsing dates from strings.
 */
public class DateUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parses a date string in LocalDate object.
     *
     * @param dateStr The date string to parse.
     * @return The parsed LocalDate object.
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DEFAULT_FORMATTER);
    }
}
