package org.example.controller;

import org.example.dto.PriceEntryDTO;
import org.example.service.PriceTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/price-trends")
public class PriceTrendController {

    private final PriceTrendService priceTrendService;

    @Autowired
    public PriceTrendController(PriceTrendService priceTrendService) {
        this.priceTrendService = priceTrendService;
    }

    @GetMapping("/product")
    public List<PriceEntryDTO> getPriceTrendsForProduct(@RequestParam String productId) {
        return priceTrendService.getPriceTrendsForProduct(productId);
    }

    @GetMapping("/product-store")
    public List<PriceEntryDTO> getPriceTrendsForProductAndStore(
            @RequestParam String productId,
            @RequestParam String storeName) {
        return priceTrendService.getPriceTrendsForProductAndStore(productId, storeName);
    }

    @GetMapping("/category")
    public List<PriceEntryDTO> getPriceTrendsByCategory(@RequestParam String category) {
        return priceTrendService.getPriceTrendsByCategory(category);
    }

    @GetMapping("/brand")
    public List<PriceEntryDTO> getPriceTrendsByBrand(@RequestParam String brand) {
        return priceTrendService.getPriceTrendsByBrand(brand);
    }
}