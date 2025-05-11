package org.example.service;

import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.ItemRepository;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscountAnalysisServiceTest {

    @Test
    public void testGetHighestDiscounts() {
        ItemRepository<Discount> mockRepo = mock(ItemRepository.class);
        DiscountAnalysisService service = new DiscountAnalysisService(mockRepo);

        LocalDate today = LocalDate.now();
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(1), today.plusDays(1), 15.0);
        Discount d2 = new Discount("P002", "Lapte", "Zuzu", 1, "l", "Lactate", today.minusDays(2), today.plusDays(2), 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getHighestDiscounts(1);

        assertEquals(1, result.size());
        assertEquals("P002", result.get(0).getProductId());
    }

    @Test
    public void testGetNewlyAddedDiscounts() {
        ItemRepository<Discount> mockRepo = mock(ItemRepository.class);
        DiscountAnalysisService service = new DiscountAnalysisService(mockRepo);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate", today, today.plusDays(5), 10.0);
        Discount d2 = new Discount("P002", "Lapte", "Zuzu", 1, "l", "Lactate", yesterday, today.plusDays(5), 15.0);
        Discount d3 = new Discount("P003", "Lapte", "Zuzu", 1, "l", "Lactate", yesterday.minusDays(2), today.plusDays(5), 20.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2, d3));

        List<DiscountDTO> result = service.getNewlyAddedDiscounts();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getProductId().equals("P001")));
        assertTrue(result.stream().anyMatch(d -> d.getProductId().equals("P002")));
    }
}
