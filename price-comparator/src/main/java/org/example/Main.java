package org.example;

import org.example.service.*;
import org.example.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final IProductService productService;
    private final PriceTrendService priceTrendService;
    private final DiscountAnalysisService discountAnalysisService;
    private final DiscountService discountService;
    private final BasketOptimizationService basketOptimizationService;
    private final PriceAlertService priceAlertService;

    @Autowired
    public Main(IProductService productService,
                PriceTrendService priceTrendService,
                DiscountAnalysisService discountAnalysisService,
                DiscountService discountService,
                BasketOptimizationService basketOptimizationService,
                PriceAlertService priceAlertService) {
        this.productService = productService;
        this.priceTrendService = priceTrendService;
        this.discountAnalysisService = discountAnalysisService;
        this.discountService = discountService;
        this.basketOptimizationService = basketOptimizationService;
        this.priceAlertService = priceAlertService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UI ui = new UI(
                productService,
                priceTrendService,
                discountAnalysisService,
                discountService,
                basketOptimizationService,
                priceAlertService
        );

        ui.run();
    }
}