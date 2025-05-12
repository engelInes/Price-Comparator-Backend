package org.example.controller;

import org.example.dto.OptimizedBasketDTO;
import org.example.dto.ShoppingListDTO;
import org.example.model.BasketItem;
import org.example.service.BasketOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketOptimizationControllerTest {

    @Mock
    private BasketOptimizationService basketOptimizationService;

    @InjectMocks
    private BasketOptimizationController basketOptimizationController;

    private List<BasketItem> testBasket;
    private OptimizedBasketDTO mockOptimizedBasket;

    @BeforeEach
    void setUp() {
        testBasket = new ArrayList<>();
        BasketItem item1 = new BasketItem("P001", "lapte", 2);
        BasketItem item2 = new BasketItem("P002", "banane", 1);
        testBasket.add(item1);
        testBasket.add(item2);

        mockOptimizedBasket = new OptimizedBasketDTO();
        ShoppingListDTO shoppingList = new ShoppingListDTO();
        shoppingList.setStoreName("Lidl");
        mockOptimizedBasket.addShoppingList(shoppingList);
        mockOptimizedBasket.setOriginalCost(10.0);
        mockOptimizedBasket.setTotalCost(8.0);
        mockOptimizedBasket.setTotalSavings(2.0);
    }

    @Test
    void testOptimizeBasket_Success() {
        when(basketOptimizationService.optimizeBasket(testBasket))
                .thenReturn(mockOptimizedBasket);

        OptimizedBasketDTO result = basketOptimizationController.optimizeBasket(testBasket);

        assertNotNull(result);
        assertEquals(1, result.getShoppingLists().size());
        assertEquals("Lidl", result.getShoppingLists().get(0).getStoreName());
        assertEquals(10.0, result.getOriginalCost());
        assertEquals(8.0, result.getTotalCost());
        assertEquals(2.0, result.getTotalSavings());

        verify(basketOptimizationService, times(1)).optimizeBasket(testBasket);
    }

    @Test
    void testOptimizeBasketWithUnitPrice_Success() {
        when(basketOptimizationService.optimizeBasketWithUnitPrice(testBasket))
                .thenReturn(mockOptimizedBasket);

        OptimizedBasketDTO result = basketOptimizationController.optimizeBasketWithUnitPrice(testBasket);

        assertNotNull(result);
        assertEquals(1, result.getShoppingLists().size());
        assertEquals("Lidl", result.getShoppingLists().get(0).getStoreName());
        assertEquals(10.0, result.getOriginalCost());
        assertEquals(8.0, result.getTotalCost());
        assertEquals(2.0, result.getTotalSavings());

        verify(basketOptimizationService, times(1)).optimizeBasketWithUnitPrice(testBasket);
    }

    @Test
    void testOptimizeBasket_EmptyBasket() {
        List<BasketItem> emptyBasket = new ArrayList<>();
        OptimizedBasketDTO emptyResult = new OptimizedBasketDTO();
        when(basketOptimizationService.optimizeBasket(emptyBasket))
                .thenReturn(emptyResult);

        OptimizedBasketDTO result = basketOptimizationController.optimizeBasket(emptyBasket);

        assertNotNull(result);
        assertTrue(result.getShoppingLists().isEmpty());

        verify(basketOptimizationService, times(1)).optimizeBasket(emptyBasket);
    }

    @Test
    void testOptimizeBasketWithUnitPrice_EmptyBasket() {
        List<BasketItem> emptyBasket = new ArrayList<>();
        OptimizedBasketDTO emptyResult = new OptimizedBasketDTO();
        when(basketOptimizationService.optimizeBasketWithUnitPrice(emptyBasket))
                .thenReturn(emptyResult);

        OptimizedBasketDTO result = basketOptimizationController.optimizeBasketWithUnitPrice(emptyBasket);

        assertNotNull(result);
        assertTrue(result.getShoppingLists().isEmpty());

        verify(basketOptimizationService, times(1)).optimizeBasketWithUnitPrice(emptyBasket);
    }

    @Test
    void testOptimizeBasket_NullBasket() {
        List<BasketItem> nullBasket = null;

        assertThrows(NullPointerException.class, () -> {
            basketOptimizationController.optimizeBasket(nullBasket);
        });
    }

    @Test
    void testOptimizeBasketWithUnitPrice_NullBasket() {
        List<BasketItem> nullBasket = null;

        assertThrows(NullPointerException.class, () -> {
            basketOptimizationController.optimizeBasketWithUnitPrice(nullBasket);
        });
    }
}