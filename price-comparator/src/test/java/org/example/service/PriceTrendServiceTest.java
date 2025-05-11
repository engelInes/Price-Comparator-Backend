package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ProductRepository;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriceTrendServiceTest {

    @Test
    public void testGetPriceTrendsForProduct() {
        ProductRepository mockRepo = mock(ProductRepository.class);
        PriceTrendService service = new PriceTrendService(mockRepo);

        List<PriceEntry> mockEntries = List.of(
                new PriceEntry("P001", "Detergent", "Curatenie", "Ariel", 1, "kg", "Lidl", LocalDate.of(2024, 1, 1), 2.99, "RON"),
                new PriceEntry("P001", "Faina", "Fainoase", "Boromir", 1, "kg", "Lidl", LocalDate.of(2023, 1, 1), 1.99, "RON")
        );

        when(mockRepo.findByProductName("P001")).thenReturn(mockEntries);

        List<PriceEntryDTO> result = service.getPriceTrendsForProduct("P001");
        assertEquals(2, result.size());
        assertEquals(1.99, result.get(0).getPrice(), 0.01);
    }
}
