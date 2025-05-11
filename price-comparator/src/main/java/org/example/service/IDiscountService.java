package org.example.service;

import org.example.dto.DiscountDTO;

import java.util.List;

public interface IDiscountService {
    List<DiscountDTO> loadDiscounts(String filePath);
    List<DiscountDTO> getMaxDiscountPerProduct(int limit);
}
