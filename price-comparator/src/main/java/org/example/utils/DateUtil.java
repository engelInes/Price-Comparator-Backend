package org.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DEFAULT_FORMATTER);
    }

    public static boolean isWithinLast24Hours(LocalDate date) {
        return !date.isBefore(LocalDate.now().minusDays(1));
    }
}
