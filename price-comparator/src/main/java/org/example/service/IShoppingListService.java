package org.example.service;

import org.example.dto.ShoppingListDTO;

import java.util.List;

public interface IShoppingListService {
    List<ShoppingListDTO> optimizeBasket(List<String> productIds);
}
