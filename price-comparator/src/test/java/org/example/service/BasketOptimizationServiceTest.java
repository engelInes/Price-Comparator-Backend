package org.example.service;

import org.example.dto.OptimizedBasketDTO;
import org.example.model.BasketItem;
import org.example.model.Discount;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketOptimizationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository<Discount> discountRepository;

    @InjectMocks
    private BasketOptimizationService basketOptimizationService;

    private List<BasketItem> testBasket;
    private List<PriceEntry> testPriceEntries;
    private List<Discount> testDiscounts;

    @BeforeEach
    void setUp() {
        testBasket = Arrays.asList(
                new BasketItem("P001", "Lapte", 2),
                new BasketItem("P002", "Paine", 1)
        );

        testPriceEntries = Arrays.asList(
                new PriceEntry("P001", "Lapte", "Lactate", "Zuzu", 1.0, "l", "Lidl", LocalDate.now(), 10.0, "RON"),
                new PriceEntry("P001", "Lapte", "Lactate", "Zuzu", 1.0, "l", "Kaufland", LocalDate.now(), 9.0, "RON"),
                new PriceEntry("P002", "Paine", "Brutarie", "panemar", 1.0, "kg", "Lidl", LocalDate.now(), 5.0, "RON"),
                new PriceEntry("P002", "Paine", "Brutarie", "panemar", 1.0, "kg", "Kaufland", LocalDate.now(), 4.5, "RON")
        );

        testDiscounts = Arrays.asList(
                new Discount("P001", "Lapte", "Lidl", 1.0, "l", "Lactate",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 10.0),
                new Discount("P002", "Paine", "Kaufland", 1.0, "kg", "Brutarie",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 5.0)
        );
    }

    @Test
    void testOptimizeBasket_Success() {
        when(productRepository.loadAllEntries()).thenReturn(testPriceEntries);
        when(productRepository.findByProductName("Lapte")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Lapte")).toList()
        );
        when(productRepository.findByProductName("Paine")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Paine")).toList()
        );
        when(productRepository.findByProductNameAndStore(anyString(), anyString())).thenAnswer(invocation ->
                testPriceEntries.stream()
                        .filter(p -> p.getProductName().equals(invocation.getArgument(0)) &&
                                p.getStoreName().equals(invocation.getArgument(1)))
                        .toList()
        );

        when(discountRepository.loadAllEntries()).thenReturn(testDiscounts);

        OptimizedBasketDTO result = basketOptimizationService.optimizeBasket(testBasket);

        assertNotNull(result);
        assertFalse(result.getShoppingLists().isEmpty());
        assertTrue(result.getTotalCost() > 0);

        result.getShoppingLists().forEach(shoppingList -> {
            if (shoppingList.getItems().get(0).getProductName().equals("Lapte")) {
                assertEquals("Kaufland", shoppingList.getStoreName());
            }
            if (shoppingList.getItems().get(0).getProductName().equals("Paine")) {
                assertEquals("Kaufland", shoppingList.getStoreName());
            }
        });
    }

    @Test
    void testOptimizeBasketWithUnitPrice_Success() {
        when(productRepository.loadAllEntries()).thenReturn(testPriceEntries);
        when(productRepository.findByProductName("Lapte")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Lapte")).toList()
        );
        when(productRepository.findByProductName("Paine")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Paine")).toList()
        );
        when(productRepository.findByProductNameAndStore(anyString(), anyString())).thenAnswer(invocation ->
                testPriceEntries.stream()
                        .filter(p -> p.getProductName().equals(invocation.getArgument(0)) &&
                                p.getStoreName().equals(invocation.getArgument(1)))
                        .toList()
        );

        when(discountRepository.loadAllEntries()).thenReturn(testDiscounts);

        OptimizedBasketDTO result = basketOptimizationService.optimizeBasketWithUnitPrice(testBasket);

        assertNotNull(result);
        assertFalse(result.getShoppingLists().isEmpty());
        assertTrue(result.getTotalCost() > 0);

        result.getShoppingLists().forEach(shoppingList -> {
            if (shoppingList.getItems().get(0).getProductName().equals("Lapte")) {
                assertEquals("Kaufland", shoppingList.getStoreName());
            }
            if (shoppingList.getItems().get(0).getProductName().equals("Paine")) {
                assertEquals("Kaufland", shoppingList.getStoreName());
            }
        });
    }

    @Test
    void testOptimizeBasket_EmptyBasket() {
        List<BasketItem> emptyBasket = Collections.emptyList();
        when(productRepository.loadAllEntries()).thenReturn(Collections.emptyList());
        when(discountRepository.loadAllEntries()).thenReturn(Collections.emptyList());

        OptimizedBasketDTO result = basketOptimizationService.optimizeBasket(emptyBasket);

        assertNotNull(result);
        assertTrue(result.getShoppingLists().isEmpty());
        assertEquals(0.0, result.getOriginalCost());
    }

    @Test
    void testOptimizeBasket_NoMatchingProducts() {
        List<BasketItem> basketWithUnknownProducts = Arrays.asList(
                new BasketItem("P999", "Noname", 1)
        );

        when(productRepository.loadAllEntries()).thenReturn(Collections.emptyList());
        when(productRepository.findByProductName("Noname")).thenReturn(Collections.emptyList());
        when(discountRepository.loadAllEntries()).thenReturn(Collections.emptyList());

        OptimizedBasketDTO result = basketOptimizationService.optimizeBasket(basketWithUnknownProducts);

        assertNotNull(result);
        assertTrue(result.getShoppingLists().isEmpty());
        assertEquals(0.0, result.getOriginalCost());
    }

    @Test
    void testCalculateOriginalCost() {
        when(productRepository.loadAllEntries()).thenReturn(testPriceEntries);
        when(productRepository.findByProductNameAndStore(anyString(), anyString())).thenAnswer(invocation ->
                testPriceEntries.stream()
                        .filter(p -> p.getProductName().equals(invocation.getArgument(0)) &&
                                p.getStoreName().equals(invocation.getArgument(1)))
                        .toList()
        );

        double originalCost = basketOptimizationService.calculateOriginalCost(testBasket);

        assertEquals(13.5, originalCost, 0.001);
    }

    @Test
    void testOptimizeBasket_MultipleDiscounts() {
        List<Discount> multipleDiscounts = Arrays.asList(
                new Discount("P001", "Lapte", "Zuzu", 1.0, "l", "lactate",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 10.0),
                new Discount("P001", "Lapte", "Zuzu", 1.0, "l", "lactate",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 15.0)
        );

        when(productRepository.loadAllEntries()).thenReturn(testPriceEntries);
        when(productRepository.findByProductName("Lapte")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Lapte")).toList()
        );
        when(productRepository.findByProductName("Paine")).thenReturn(
                testPriceEntries.stream().filter(p -> p.getProductName().equals("Paine")).toList()
        );
        when(productRepository.findByProductNameAndStore(anyString(), anyString())).thenAnswer(invocation ->
                testPriceEntries.stream()
                        .filter(p -> p.getProductName().equals(invocation.getArgument(0)) &&
                                p.getStoreName().equals(invocation.getArgument(1)))
                        .toList()
        );

        when(discountRepository.loadAllEntries()).thenReturn(multipleDiscounts);

        OptimizedBasketDTO result = basketOptimizationService.optimizeBasket(testBasket);

        assertNotNull(result);
        assertFalse(result.getShoppingLists().isEmpty());

        result.getShoppingLists().forEach(shoppingList -> {
            if (shoppingList.getItems().get(0).getProductName().equals("Lapte")) {
                assertTrue(shoppingList.getItems().get(0).getSavings() > 1.0);
            }
        });
    }
}