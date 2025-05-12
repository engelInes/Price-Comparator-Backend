package org.example.service;

import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscountAnalysisServiceTest {

    private ItemRepository<Discount> mockRepo;
    private DiscountAnalysisService service;
    private LocalDate today;

    @Before
    public void setUp() {
        mockRepo = mock(ItemRepository.class);
        service = new DiscountAnalysisService(mockRepo);
        today = LocalDate.now();
    }
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

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getProductId().equals("P001")));
    }

    @Test
    public void testGetHighestDiscounts_EmptyRepository() {
        when(mockRepo.loadAllEntries()).thenReturn(Collections.emptyList());

        List<DiscountDTO> result = service.getHighestDiscounts(5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetHighestDiscounts_NoValidDiscounts() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(10), today.minusDays(5), 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.minusDays(8), today.minusDays(2), 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getHighestDiscounts(3);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetHighestDiscounts_FutureDiscounts() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.plusDays(5), today.plusDays(10), 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.plusDays(2), today.plusDays(8), 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getHighestDiscounts(3);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetHighestDiscounts_LimitGreaterThanAvailable() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(1), today.plusDays(1), 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.minusDays(2), today.plusDays(2), 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getHighestDiscounts(5);

        assertEquals(2, result.size());
        assertEquals("P002", result.get(0).getProductId());
        assertEquals("P001", result.get(1).getProductId());
    }

    @Test
    public void testGetHighestDiscounts_MixedValidAndInvalidDiscounts() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(1), today.plusDays(1), 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.minusDays(5), today.minusDays(1), 25.0);
        Discount d3 = new Discount("P003", "Branza", "Hochland", 1, "l", "Lactate",
                today, today.plusDays(5), 20.0);
        Discount d4 = new Discount("P004", "Sana", "Covalact", 1, "l", "Lactate",
                today.plusDays(1), today.plusDays(6), 30.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2, d3, d4));

        List<DiscountDTO> result = service.getHighestDiscounts(3);

        assertEquals(2, result.size());
        assertEquals("P003", result.get(0).getProductId());
        assertEquals("P001", result.get(1).getProductId());
    }

    @Test
    public void testGetHighestDiscounts_CorrectSorting() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(1), today.plusDays(1), 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.minusDays(2), today.plusDays(2), 25.0);
        Discount d3 = new Discount("P003", "Branza", "Hochland", 1, "l", "Lactate",
                today, today.plusDays(5), 20.0);
        Discount d4 = new Discount("P004", "Sana", "Covalact", 1, "l", "Lactate",
                today.minusDays(3), today.plusDays(3), 10.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2, d3, d4));

        List<DiscountDTO> result = service.getHighestDiscounts(4);

        assertEquals(4, result.size());
        assertEquals("P002", result.get(0).getProductId());
        assertEquals("P003", result.get(1).getProductId());
        assertEquals("P001", result.get(2).getProductId());
        assertEquals("P004", result.get(3).getProductId());
    }

    @Test
    public void testGetNewlyAddedDiscounts_NoNewDiscounts() {
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                yesterday, tomorrow, 15.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                yesterday.minusDays(1), tomorrow, 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getNewlyAddedDiscounts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetNewlyAddedDiscounts_MultipleTodayDiscounts() {
        LocalDate yesterday = today.minusDays(1);

        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today, today.plusDays(5), 10.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today, today.plusDays(3), 15.0);
        Discount d3 = new Discount("P003", "Branza", "Hochland", 1, "l", "Lactate",
                yesterday, today.plusDays(5), 20.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2, d3));

        List<DiscountDTO> result = service.getNewlyAddedDiscounts();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getProductId().equals("P001")));
        assertTrue(result.stream().anyMatch(d -> d.getProductId().equals("P002")));
    }

    @Test
    public void testGetNewlyAddedDiscounts_EmptyRepository() {
        when(mockRepo.loadAllEntries()).thenReturn(Collections.emptyList());

        List<DiscountDTO> result = service.getNewlyAddedDiscounts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetHighestDiscounts_SameDiscountPercentage() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today.minusDays(1), today.plusDays(1), 20.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today.minusDays(2), today.plusDays(2), 20.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2));

        List<DiscountDTO> result = service.getHighestDiscounts(2);

        assertEquals(2, result.size());
        assertEquals(20.0, result.get(0).getPercentageOfDiscount(), 0.001);
        assertEquals(20.0, result.get(1).getPercentageOfDiscount(), 0.001);
    }

    @Test
    public void testGetNewlyAddedDiscounts_SortingByDate() {
        Discount d1 = new Discount("P001", "Lapte", "Zuzu", 1, "l", "Lactate",
                today, today.plusDays(5), 10.0);
        Discount d2 = new Discount("P002", "Iaurt", "Danone", 1, "l", "Lactate",
                today, today.plusDays(3), 15.0);

        List<Discount> discounts = new ArrayList<>();
        discounts.add(d2);
        discounts.add(d1);

        when(mockRepo.loadAllEntries()).thenReturn(discounts);

        List<DiscountDTO> result = service.getNewlyAddedDiscounts();

        assertEquals(2, result.size());
    }
}
