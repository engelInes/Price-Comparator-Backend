package org.example.controller;

import org.example.dto.DiscountDTO;
import org.example.service.DiscountAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discount-analysis")
public class DiscountAnalysisController {

    private final DiscountAnalysisService discountAnalysisService;

    @Autowired
    public DiscountAnalysisController(DiscountAnalysisService discountAnalysisService) {
        this.discountAnalysisService = discountAnalysisService;
    }

    @GetMapping("/highest")
    public List<DiscountDTO> getHighestDiscounts(@RequestParam(defaultValue = "10") int limit) {
        return discountAnalysisService.getHighestDiscounts(limit);
    }

    @GetMapping("/new")
    public List<DiscountDTO> getNewlyAddedDiscounts() {
        return discountAnalysisService.getNewlyAddedDiscounts();
    }
}