package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ItemRepository<PriceEntry> mockRepo;
    private ProductService service;
    private List<PriceEntry> sampleEntries;

    @Before
    public void setUp() {
        mockRepo = mock(ItemRepository.class);
        service = new ProductService(mockRepo);

        sampleEntries = List.of(
                new PriceEntry("P001", "Detergent", "Curatenie", "Ariel", 1, "kg", "Lidl", LocalDate.of(2023, 1, 1), 1.99, "RON"),
                new PriceEntry("P002", "Paine", "Brutarie", "Boromir", 1, "kg", "Lidl", LocalDate.of(2023, 2, 10), 1.50, "RON"),
                new PriceEntry("P003", "Lapte", "Lactate", "Zuzu", 1, "l", "Kaufland", LocalDate.of(2023, 3, 15), 4.50, "RON")
        );
    }
    @Test
    public void testLoadPriceEntries() {
        when(mockRepo.loadEntriesFromFile("test_prices.csv")).thenReturn(List.of(sampleEntries.get(0)));

        List<PriceEntryDTO> result = service.loadPriceEntries("test_prices.csv");

        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getProductId());
        assertEquals("Detergent", result.get(0).getProductName());
        verify(mockRepo, times(1)).loadEntriesFromFile("test_prices.csv");
    }

    @Test
    public void testLoadPriceEntriesWithEmptyFile() {
        when(mockRepo.loadEntriesFromFile("empty.csv")).thenReturn(Collections.emptyList());

        List<PriceEntryDTO> result = service.loadPriceEntries("empty.csv");

        assertTrue(result.isEmpty());
        verify(mockRepo, times(1)).loadEntriesFromFile("empty.csv");
    }

    @Test
    public void testLoadPriceEntriesWithNullResponse() {
        when(mockRepo.loadEntriesFromFile("null_file.csv")).thenReturn(null);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.loadPriceEntries("null_file.csv")
        );

        verify(mockRepo, times(1)).loadEntriesFromFile("null_file.csv");
    }

    @Test
    public void testLoadLargePriceEntryDataset() {
        List<PriceEntry> largeDataset = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeDataset.add(new PriceEntry(
                    "P" + i,
                    "Product" + i,
                    "Category" + (i % 10),
                    "Brand" + (i % 20),
                    1,
                    "unit",
                    "Store" + (i % 5),
                    LocalDate.of(2023, (i % 12) + 1, (i % 28) + 1),
                    10.0 + (i % 100) / 10.0,
                    "RON"
            ));
        }

        when(mockRepo.loadEntriesFromFile("large_dataset.csv")).thenReturn(largeDataset);

        List<PriceEntryDTO> result = service.loadPriceEntries("large_dataset.csv");

        assertEquals(1000, result.size());
        verify(mockRepo, times(1)).loadEntriesFromFile("large_dataset.csv");
    }

    @Test
    public void testLoadPriceEntriesWithExceptionInRepository() {
        when(mockRepo.loadEntriesFromFile("invalid.csv")).thenThrow(new RuntimeException("File not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.loadPriceEntries("invalid.csv")
        );

        assertEquals("File not found", exception.getMessage());
        verify(mockRepo, times(1)).loadEntriesFromFile("invalid.csv");
    }

}