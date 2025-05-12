package org.example.service;

import org.example.dto.ShoppingListDTO;

import java.util.List;

/**
 * Service interface for optimizing a shopping basket.
 */
public interface IShoppingListService {
    /**
     * Optimizes a basket based on a list of product IDs.
     *
     * @param productIds List of product IDs to include in the basket.
     * @return An optimized shopping list as a list.
     */
    List<ShoppingListDTO> optimizeBasket(List<String> productIds);
}
