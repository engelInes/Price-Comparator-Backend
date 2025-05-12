package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PriceTrendServiceTest {

    private ProductRepository mockRepo;
    private PriceTrendService service;
    private List<PriceEntry> sampleEntries;

    @Before
    public void setUp() {
        mockRepo = mock(ProductRepository.class);
        service = new PriceTrendService(mockRepo);

        sampleEntries = List.of(
                new PriceEntry("P001", "Detergent", "Curatare", "Ariel", 1, "kg", "Lidl", LocalDate.of(2023, 1, 1), 1.99, "RON"),
                new PriceEntry("P001", "Detergent", "Curatare", "Ariel", 1, "kg", "Lidl", LocalDate.of(2023, 6, 1), 2.49, "RON"),
                new PriceEntry("P001", "Detergent", "Curatare", "Ariel", 1, "kg", "Lidl", LocalDate.of(2024, 1, 1), 2.99, "RON"),
                new PriceEntry("P001", "Detergent", "Curatare", "Ariel", 1, "kg", "Kaufland", LocalDate.of(2023, 1, 5), 2.05, "RON"),
                new PriceEntry("P001", "Detergent", "Curatare", "Ariel", 1, "kg", "Kaufland", LocalDate.of(2024, 1, 5), 3.10, "RON"),
                new PriceEntry("P002", "Faina", "Fainoase", "Boromir", 1, "kg", "Lidl", LocalDate.of(2023, 1, 10), 1.50, "RON"),
                new PriceEntry("P002", "Faina", "Fainoase", "Boromir", 1, "kg", "Lidl", LocalDate.of(2024, 1, 10), 1.95, "RON"),
                new PriceEntry("P003", "Lapte", "Lactate", "Zuzu", 1, "l", "Kaufland", LocalDate.of(2023, 3, 15), 4.50, "RON"),
                new PriceEntry("P003", "Lapte", "Lactate", "Napolact", 1, "l", "Kaufland", LocalDate.of(2023, 3, 20), 5.20, "RON")
        );
    }
    @Test
    public void testGetPriceTrendsForProduct() {
        List<PriceEntry> productEntries = List.of(
                sampleEntries.get(0),
                sampleEntries.get(1),
                sampleEntries.get(2),
                sampleEntries.get(3),
                sampleEntries.get(4)
        );
        when(mockRepo.findByProductName("P001")).thenReturn(productEntries);

        List<PriceEntryDTO> result = service.getPriceTrendsForProduct("P001");

        assertEquals(5, result.size());
        verify(mockRepo, times(1)).findByProductName("P001");
    }

    @Test
    public void testGetPriceTrendsForProductWithNoEntries() {
        when(mockRepo.findByProductName("Noname")).thenReturn(Collections.emptyList());
        List<PriceEntryDTO> result = service.getPriceTrendsForProduct("Noname");
        assertTrue(result.isEmpty());
        verify(mockRepo, times(1)).findByProductName("Noname");
    }

    @Test
    public void testGetPriceTrendsByCategory() {
        List<PriceEntry> categoryEntries = List.of(
                sampleEntries.get(0),
                sampleEntries.get(1),
                sampleEntries.get(2),
                sampleEntries.get(3),
                sampleEntries.get(4)
        );
        when(mockRepo.findByProductCategory("Curatare")).thenReturn(categoryEntries);

        List<PriceEntryDTO> result = service.getPriceTrendsByCategory("Curatare");
        assertEquals(5, result.size());

        verify(mockRepo, times(1)).findByProductCategory("Curatare");
    }

    @Test
    public void testGetPriceTrendsByCategoryWithNoEntries() {
        when(mockRepo.findByProductCategory("Noname")).thenReturn(Collections.emptyList());

        List<PriceEntryDTO> result = service.getPriceTrendsByCategory("Noname");

        assertTrue(result.isEmpty());
        verify(mockRepo, times(1)).findByProductCategory("Noname");
    }

    @Test
    public void testGetPriceTrendsByBrand() {
        List<PriceEntry> brandEntries = List.of(
                sampleEntries.get(0),
                sampleEntries.get(1),
                sampleEntries.get(2),
                sampleEntries.get(3),
                sampleEntries.get(4)
        );
        when(mockRepo.findByBrand("Ariel")).thenReturn(brandEntries);

        List<PriceEntryDTO> result = service.getPriceTrendsByBrand("Ariel");
        assertEquals(5, result.size());

        verify(mockRepo, times(1)).findByBrand("Ariel");
    }

    @Test
    public void testGetPriceTrendsByBrandWithNoEntries() {
        when(mockRepo.findByBrand("Noname")).thenReturn(Collections.emptyList());

        List<PriceEntryDTO> result = service.getPriceTrendsByBrand("Noname");

        assertTrue(result.isEmpty());
        verify(mockRepo, times(1)).findByBrand("Noname");
    }

    @Test
    public void testMultipleBrandsInSameCategory() {
        List<PriceEntry> dairyEntries = List.of(
                sampleEntries.get(7),
                sampleEntries.get(8)
        );
        when(mockRepo.findByProductCategory("Lactate")).thenReturn(dairyEntries);

        List<PriceEntryDTO> result = service.getPriceTrendsByCategory("Lactate");

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2023, 3, 15), result.get(0).getDate());
        assertEquals(LocalDate.of(2023, 3, 20), result.get(1).getDate());

        verify(mockRepo, times(1)).findByProductCategory("Lactate");
    }
}
