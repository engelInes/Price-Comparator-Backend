package org.example.controller;

import org.example.dto.PriceEntryDTO;
import org.example.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductService();
    }

    @GetMapping("/load")
    public List<PriceEntryDTO> loadPriceEntries(@RequestParam String filePath) {
        return productService.loadPriceEntries(filePath);
    }
}