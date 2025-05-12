package org.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PriceEntryTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        PriceEntry entry = new PriceEntry("P005", "paine", "brutarie", "lacasa", 0.75, "kg", "lidl", date, 2.99, "RON");

        assertEquals("P005", entry.getProductId());
        assertEquals("paine", entry.getProductName());
        assertEquals("brutarie", entry.getProductCategory());
        assertEquals("lacasa", entry.getBrand());
        assertEquals(0.75, entry.getPackageQuantity());
        assertEquals("kg", entry.getPackageUnit());
        assertEquals("lidl", entry.getStoreName());
        assertEquals(date, entry.getDate());
        assertEquals(2.99, entry.getPrice());
        assertEquals("RON", entry.getCurrency());
    }

    @Test
    void testSetters() {
        LocalDate date = LocalDate.now();
        PriceEntry entry = new PriceEntry();

        entry.setProductId("P006");
        entry.setProductName("paine");
        entry.setProductCategory("brutarie");
        entry.setBrand("lacasa");
        entry.setPackageQuantity(0.75);
        entry.setPackageUnit("kg");
        entry.setStoreName("lidl");
        entry.setDate(date);
        entry.setPrice(3.49);
        entry.setCurrency("RON");

        assertEquals("P006", entry.getProductId());
        assertEquals("paine", entry.getProductName());
        assertEquals("brutarie", entry.getProductCategory());
        assertEquals("lacasa", entry.getBrand());
        assertEquals(0.75, entry.getPackageQuantity());
        assertEquals("kg", entry.getPackageUnit());
        assertEquals("lidl", entry.getStoreName());
        assertEquals(date, entry.getDate());
        assertEquals(3.49, entry.getPrice());
        assertEquals("RON", entry.getCurrency());
    }
}
