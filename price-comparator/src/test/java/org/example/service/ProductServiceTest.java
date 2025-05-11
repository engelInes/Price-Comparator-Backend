package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Test
    public void testLoadPriceEntries() {
        ItemRepository<PriceEntry> mockRepo = Mockito.mock(ItemRepository.class);
        ProductService service = new ProductService(mockRepo);

        PriceEntry entry = new PriceEntry();
        when(mockRepo.loadEntriesFromFile("test_prices.csv")).thenReturn(List.of(entry));

        List<PriceEntryDTO> result = service.loadPriceEntries("test_prices.csv");

        assertEquals(1, result.size());
        verify(mockRepo, times(1)).loadEntriesFromFile("test_prices.csv");
    }
}