package org.example.controller;

import org.example.dto.DiscountDTO;
import org.example.service.DiscountService;
import org.example.service.IDiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final IDiscountService discountService;

    public DiscountController(IDiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/load")
    public List<DiscountDTO> loadDiscounts(@RequestParam String filePath) {
        return discountService.loadDiscounts(filePath);
    }

    @GetMapping("/discounts/top-max-per-product")
    public ResponseEntity<List<DiscountDTO>> getMaxDiscountPerProduct(@RequestParam(defaultValue = "10") int limit) {
        List<DiscountDTO> discounts = discountService.getMaxDiscountPerProduct(limit);
        return ResponseEntity.ok(discounts);
    }
}
