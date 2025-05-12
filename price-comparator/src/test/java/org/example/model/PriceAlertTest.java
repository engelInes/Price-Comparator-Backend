package org.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PriceAlertTest {

    @Test
    void testSettersAndGetters() {
        PriceAlert alert = new PriceAlert();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime triggered = now.plusDays(2);

        alert.setId(101L);
        alert.setUserId("U123");
        alert.setProductId("P007");
        alert.setTargetPrice(15.5);
        alert.setActive(true);
        alert.setCreatedAt(now);
        alert.setTriggeredAt(triggered);

        assertEquals(101L, alert.getId());
        assertEquals("U123", alert.getUserId());
        assertEquals("P007", alert.getProductId());
        assertEquals(15.5, alert.getTargetPrice());
        assertTrue(alert.isActive());
        assertEquals(now, alert.getCreatedAt());
        assertEquals(triggered, alert.getTriggeredAt());
    }
}
