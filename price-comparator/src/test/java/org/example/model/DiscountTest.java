package org.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DiscountTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 2, 1);

        Discount discount = new Discount("P003", "lapte", "zuzu", 1.5, "l", "lactate", start, end, 10.0);
        assertEquals("P003", discount.getProductId());
        assertEquals("lapte", discount.getProductName());
        assertEquals("zuzu", discount.getBrand());
        assertEquals(1.5, discount.getPackageQuantity());
        assertEquals("l", discount.getPackageUnit());
        assertEquals("lactate", discount.getProductCategory());
        assertEquals(start, discount.getStartingDate());
        assertEquals(end, discount.getEndingDate());
        assertEquals(10.0, discount.getPercentageOfDiscount());
    }

    @Test
    void testSetters() {
        Discount discount = new Discount();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        discount.setProductId("P004");
        discount.setProductName("banane");
        discount.setBrand("selgros");
        discount.setPackageQuantity(0.5);
        discount.setPackageUnit("kg");
        discount.setProductCategory("fructe");
        discount.setStartingDate(start);
        discount.setEndingDate(end);
        discount.setPercentageOfDiscount(15.0);

        assertEquals("P004", discount.getProductId());
        assertEquals("banane", discount.getProductName());
        assertEquals("selgros", discount.getBrand());
        assertEquals(0.5, discount.getPackageQuantity());
        assertEquals("kg", discount.getPackageUnit());
        assertEquals("fructe", discount.getProductCategory());
        assertEquals(start, discount.getStartingDate());
        assertEquals(end, discount.getEndingDate());
        assertEquals(15.0, discount.getPercentageOfDiscount());
    }
}
