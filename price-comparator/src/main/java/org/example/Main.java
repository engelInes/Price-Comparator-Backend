package org.example;

import org.example.controller.*;
import org.example.service.*;
import org.example.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final PriceTrendController priceTrendController;
    private final BasketOptimizationController basketOptimizationController;
    private final DiscountAnalysisController discountAnalysisController;
    private final DiscountController discountController;
    private final PriceAlertController priceAlertController;
    private final PriceAlertService priceAlertService;

    @Autowired
    public Main(PriceTrendController priceTrendController,
                BasketOptimizationController basketOptimizationController,
                DiscountAnalysisController discountAnalysisController,
                DiscountController discountController,
                PriceAlertController priceAlertController,
                PriceAlertService priceAlertService) {
        this.priceTrendController = priceTrendController;
        this.basketOptimizationController = basketOptimizationController;
        this.discountAnalysisController = discountAnalysisController;
        this.discountController = discountController;
        this.priceAlertController = priceAlertController;
        this.priceAlertService = priceAlertService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UI ui = new UI(
                priceTrendController,
                basketOptimizationController,
                discountAnalysisController,
                discountController,
                priceAlertController,
                priceAlertService
        );

        ui.run();
    }
}