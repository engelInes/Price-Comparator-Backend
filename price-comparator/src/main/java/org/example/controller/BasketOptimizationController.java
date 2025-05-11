package org.example.controller;

import org.example.dto.OptimizedBasketDTO;
import org.example.model.BasketItem;
import org.example.service.BasketOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/basket")
public class BasketOptimizationController {

    private final BasketOptimizationService basketOptimizationService;

    @Autowired
    public BasketOptimizationController(BasketOptimizationService basketOptimizationService) {
        this.basketOptimizationService = basketOptimizationService;
    }

    @PostMapping("/optimize")
    public OptimizedBasketDTO optimizeBasket(@RequestBody List<BasketItem> basket) {
        return basketOptimizationService.optimizeBasket(basket);
    }

    @PostMapping("/optimizeWithUnitPrice")
    public OptimizedBasketDTO optimizeBasketWithUnitPrice(@RequestBody List<BasketItem> basket) {
        return basketOptimizationService.optimizeBasketWithUnitPrice(basket);
    }
}