package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testSettersAndGetters() {
        Product product = new Product();
        product.setProductId("P008");
        product.setProductName("iaurt");
        product.setProductCategory("lactate");
        product.setBrand("zuzu");
        product.setPackageQuantity(0.2);
        product.setPackageUnit("l");
        product.setPrice(1.99);
        product.setCurrency("RON");

        assertEquals("P008", product.getProductId());
        assertEquals("iaurt", product.getProductName());
        assertEquals("lactate", product.getProductCategory());
        assertEquals("zuzu", product.getBrand());
        assertEquals(0.2, product.getPackageQuantity());
        assertEquals("l", product.getPackageUnit());
        assertEquals(1.99, product.getPrice());
        assertEquals("RON", product.getCurrency());
    }
}
