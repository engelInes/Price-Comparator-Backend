package org.example.service;

import org.example.dto.DiscountDTO;

import java.util.List;

/**
 * Service interface for handling discount-related operations.
 */
public interface IDiscountService {
    /**
     * Loads discounts from a file and converts them to DTOs.
     *
     * @param filePath The file path containing discount data.
     * @return A list of DiscountDTO objects.
     */
    List<DiscountDTO> loadDiscounts(String filePath);

    /**
     * Retrieves the highest discount for each product up to a specified limit.
     *
     * @param limit The maximum number of top discounts to return.
     * @return A list of DiscountDTO with the maximum discount per product.
     */
    List<DiscountDTO> getMaxDiscountPerProduct(int limit);
}
