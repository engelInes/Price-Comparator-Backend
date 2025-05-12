package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BasketItemTest {

    @Test
    void testConstructorAndGetters() {
        BasketItem item = new BasketItem("P001", "lapte", 3);
        assertEquals("P001", item.getProductId());
        assertEquals("lapte", item.getProductName());
        assertEquals(3, item.getQuantity());
    }

    @Test
    void testSetters() {
        BasketItem item = new BasketItem();
        item.setProductId("P002");
        item.setProductName("banane");
        item.setQuantity(5);

        assertEquals("P002", item.getProductId());
        assertEquals("banane", item.getProductName());
        assertEquals(5, item.getQuantity());
    }
}
