package org.example.ui;

import org.example.controller.*;
import org.example.dto.*;
import org.example.model.BasketItem;
import org.example.service.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UI {
    private final PriceTrendController priceTrendController;
    private final BasketOptimizationController basketOptimizationController;
    private final DiscountAnalysisController discountAnalysisController;
    private final DiscountController discountController;
    private final PriceAlertController priceAlertController;
    private final PriceAlertService priceAlertService;

    private final ConcurrentMap<String, WatchService> fileWatchers = new ConcurrentHashMap<>();

    private static final String DEFAULT_USER_ID = "user1";
    private static final String CSV_DIRECTORY = "src/main/resources/data";

    public UI(PriceTrendController priceTrendController,
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

    public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);

        startWatcher();

        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Optimize shopping basket savings");
            System.out.println("2. Show price trend for a product");
            System.out.println("3. Show price trend for a product in a store");
            System.out.println("4. Show price trend by category");
            System.out.println("5. Show price trend by brand");
            System.out.println("6. Show top discounts of last 24h");
            System.out.println("7. Show max discounts");
            System.out.println("8. Optimize shopping basket with unit price");
            System.out.println("9. Create Price Alert");
            System.out.println("10. View Price Alerts");
            System.out.println("11. View Triggered Price Alerts");
            System.out.println("12. Delete Price Alert");
            System.out.println("13. Exit");

            System.out.print("Enter choice: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> handleBasketOptimization(scanner);
                case "2" -> handleProductTrend(scanner);
                case "3" -> handleProductStoreTrend(scanner);
                case "4" -> handleCategoryTrend(scanner);
                case "5" -> handleBrandTrend(scanner);
                case "6" -> handleTopDiscounts(scanner);
                case "7" -> handleMaxDiscounts(scanner);
                case "8" -> handleBasketOptimizationWithUnitPrice(scanner);
                case "9" -> handleCreatePriceAlert(scanner);
                case "10" -> handleViewPriceAlerts(scanner);
                case "11" -> handleViewTriggeredAlerts(scanner);
                case "12" -> handleDeletePriceAlert(scanner);
                case "13" -> {
                    System.out.println("Exiting...");
                    closeAllWatchServices();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void startWatcher() {
        try {
            Path directoryPath = Paths.get(CSV_DIRECTORY);

            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("Created directory: " + directoryPath);
            }

            WatchService watchService = FileSystems.getDefault().newWatchService();
            directoryPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            Thread watcherThread = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                            Path filename = pathEvent.context();

                            if (filename.toString().toLowerCase().endsWith(".csv")) {
                                Path fullPath = directoryPath.resolve(filename);
                                System.out.println("CSV file detected: " + fullPath);

                                Thread.sleep(500);

                                checkPriceAlerts();
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Directory watcher interrupted: " + e.getMessage());
                }
            });

            watcherThread.setDaemon(true);
            watcherThread.start();
            System.out.println("Started monitoring CSV files in: " + directoryPath);
        } catch (IOException e) {
            System.err.println("Error setting up directory watcher: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkPriceAlerts() {
        System.out.println("Checking for triggered price alerts...");

        try {
            priceAlertService.checkPriceAlerts();

            List<PriceAlertDTO> triggeredAlerts = priceAlertController.getTriggeredAlerts(DEFAULT_USER_ID);

            if (!triggeredAlerts.isEmpty()) {
                System.out.println("PRICE ALERT NOTIFICATION");
                System.out.println("--------------------------------");
                for (PriceAlertDTO alert : triggeredAlerts) {
                    System.out.printf("PRICE DROP ALERT: %s is now at %.2fRON (below your target of %.2fRON)!%n",
                            alert.getProductName(), alert.getCurrentPrice(), alert.getTargetPrice());
                    System.out.printf("Available at: %s%n", alert.getStoreName());
                    System.out.println("--------------------------------");
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting alert " + e.getMessage());
        }
    }

    private void closeAllWatchServices() {
        for (WatchService watchService : fileWatchers.values()) {
            try {
                watchService.close();
            } catch (IOException e) {
                System.err.println("Error closing watch service: " + e.getMessage());
            }
        }
        fileWatchers.clear();
    }

    private void handleProductTrend(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();

        System.out.printf("searching for %s", productName);
        try {
            List<PriceEntryDTO> trend = priceTrendController.getPriceTrendsForProduct(productName);
            if (trend.isEmpty()) {
                System.out.println("No trend data found.");
            } else {
                trend.forEach(e ->
                        System.out.printf("%s: %.2fRON (%s)%n", e.getProductName(), e.getPrice(), e.getDate()));
            }
        } catch (Exception e) {
            System.err.println("Error getting product trend: " + e.getMessage());
        }
    }

    private void handleProductStoreTrend(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter store name: ");
        String store = scanner.nextLine();

        try {
            List<PriceEntryDTO> trend = priceTrendController.getPriceTrendsForProductAndStore(productName, store);
            if (trend.isEmpty()) {
                System.out.println("No trend data found for that product in the specified store.");
            } else {
                trend.forEach(e ->
                        System.out.printf("%s: %.2fRON (%s) in %s%n", e.getProductName(), e.getPrice(), e.getDate(), e.getStoreName()));
            }
        } catch (Exception e) {
            System.err.println("Error getting product store trend: " + e.getMessage());
        }
    }

    private void handleCategoryTrend(Scanner scanner) {
        System.out.print("Enter product category: ");
        String category = scanner.nextLine();

        try {
            List<PriceEntryDTO> trend = priceTrendController.getPriceTrendsByCategory(category);
            if (trend.isEmpty()) {
                System.out.println("No trend data found for that category.");
            } else {
                trend.forEach(e ->
                        System.out.printf("%s: %.2fRON (%s) [%s]%n", e.getProductName(), e.getPrice(), e.getDate(), e.getStoreName()));
            }
        } catch (Exception e) {
            System.err.println("Error getting category trend: " + e.getMessage());
        }
    }

    private void handleBrandTrend(Scanner scanner) {
        System.out.print("Enter brand name: ");
        String brand = scanner.nextLine();

        try {
            List<PriceEntryDTO> trend = priceTrendController.getPriceTrendsByBrand(brand);
            if (trend.isEmpty()) {
                System.out.println("No trend data found for that brand.");
            } else {
                trend.forEach(e ->
                        System.out.printf("%s: %.2fRON (%s) [%s]%n", e.getProductId(), e.getPrice(), e.getDate(), e.getStoreName()));
            }
        } catch (Exception e) {
            System.err.println("Error getting brand trend: " + e.getMessage());
        }
    }

    private void handleTopDiscounts(Scanner scanner) {
        System.out.print("Enter how many top discounts to display (e.g., 5): ");
        int limit;
        try {
            limit = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        try {
            List<DiscountDTO> topDiscounts = discountAnalysisController.getHighestDiscounts(limit);
            if (topDiscounts.isEmpty()) {
                System.out.println("No discounts found.");
            } else {
                System.out.println("Top Discounts:");
                topDiscounts.forEach(d -> System.out.printf(
                        "%s (%s, %.0f%s %s): %.2f%% off from %s to %s%n",
                        d.getProductName(),
                        d.getBrand(),
                        d.getPackageQuantity(),
                        d.getPackageUnit(),
                        d.getProductCategory(),
                        d.getPercentageOfDiscount(),
                        d.getStartingDate(),
                        d.getEndingDate()
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting top discounts: " + e.getMessage());
        }
    }

    private void handleMaxDiscounts(Scanner scanner) {
        System.out.print("Enter how many max discounts to display (e.g., 5): ");
        int limit;
        try {
            limit = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        try {
            List<DiscountDTO> maxDiscounts = discountController.getMaxDiscountPerProduct(limit).getBody();
            if (maxDiscounts.isEmpty()) {
                System.out.println("No maximum discounts found.");
            } else {
                System.out.println("Maximum Discounts (best per product across all stores):");
                maxDiscounts.forEach(d -> System.out.printf(
                        "%s (%s, %.0f%s %s): %.2f%% off from %s to %s%n",
                        d.getProductName(),
                        d.getBrand(),
                        d.getPackageQuantity(),
                        d.getPackageUnit(),
                        d.getProductCategory(),
                        d.getPercentageOfDiscount(),
                        d.getStartingDate(),
                        d.getEndingDate()
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting max discounts: " + e.getMessage());
        }
    }

    private void handleBasketOptimization(Scanner scanner) {
        List<BasketItem> basket = new ArrayList<>();

        System.out.println("Enter products with quantity (e.g., Milk:2, Eggs:1):");
        String input = scanner.nextLine();

        String[] items = input.split(",");
        for (String item : items) {
            String[] parts = item.trim().split(":");
            if (parts.length != 2) continue;

            String name = parts[0].trim();
            int qty;
            try {
                qty = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity for: " + name);
                continue;
            }

            List<PriceEntryDTO> priceEntries = priceTrendController.getPriceTrendsForProduct(name);
            if (priceEntries.isEmpty()) {
                System.out.println("Product not found: " + name);
                continue;
            }

            String productId = priceEntries.get(0).getProductName();
            basket.add(new BasketItem(productId, name, qty));
        }

        if (basket.isEmpty()) {
            System.out.println("No valid items provided.");
            return;
        }

        try {
            OptimizedBasketDTO optimized = basketOptimizationController.optimizeBasket(basket);

            System.out.printf("\nOriginal Cost: %.2fRON%n", optimized.getOriginalCost());
            System.out.printf("Optimized Cost: %.2fRON%n", optimized.getTotalCost());
            System.out.printf("Total Savings: %.2fRON%n", optimized.getTotalSavings());

            for (ShoppingListDTO list : optimized.getShoppingLists()) {
                System.out.println("\nStore: " + list.getStoreName());
                System.out.printf("Store Total: %.2fRON%n", list.getTotalCost());
                System.out.printf("Store Savings: %.2fRON%n", list.getTotalSavings());

                System.out.println("Items:");
                for (BasketItemDTO item : list.getItems()) {
                    System.out.printf(" - %s:%n", item.getProductName());
                    System.out.printf("   Quantity: %d%n", item.getQuantity());
                    System.out.printf("   Total Price: %.2fRON%n", item.getPrice());
                    System.out.printf("   Savings: %.2fRON%n", item.getSavings());
                }
            }
        } catch (Exception e) {
            System.err.println("Error optimizing basket: " + e.getMessage());
        }
    }

    private void handleBasketOptimizationWithUnitPrice(Scanner scanner) {
        List<BasketItem> basket = new ArrayList<>();

        System.out.println("Enter products with quantity (e.g., Milk:2, Eggs:1):");
        String input = scanner.nextLine();

        String[] items = input.split(",");
        for (String item : items) {
            String[] parts = item.trim().split(":");
            if (parts.length != 2) continue;

            String name = parts[0].trim();
            int qty;
            try {
                qty = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity for: " + name);
                continue;
            }

            List<PriceEntryDTO> priceEntries = priceTrendController.getPriceTrendsForProduct(name);
            if (priceEntries.isEmpty()) {
                System.out.println("Product not found: " + name);
                continue;
            }

            String productId = priceEntries.get(0).getProductName();
            basket.add(new BasketItem(productId, name, qty));
        }

        if (basket.isEmpty()) {
            System.out.println("No valid items provided.");
            return;
        }

        try {
            OptimizedBasketDTO optimized = basketOptimizationController.optimizeBasketWithUnitPrice(basket);

            System.out.printf("\nOriginal Cost: %.2fRON%n", optimized.getOriginalCost());
            System.out.printf("Optimized Cost: %.2fRON%n", optimized.getTotalCost());
            System.out.printf("Total Savings: %.2fRON%n", optimized.getTotalSavings());

            for (ShoppingListDTO list : optimized.getShoppingLists()) {
                System.out.println("\nStore: " + list.getStoreName());
                for (BasketItemDTO item : list.getItems()) {
                    System.out.printf(" - %s: %.2fRON (Qty: %d, Saved: %.2fRON) %s %.2fRON per unit%n",
                            item.getProductName(), item.getPrice(), item.getQuantity(), item.getSavings(),
                            item.getUnitPriceLabel(), item.getUnitPrice());
                }
            }
        } catch (Exception e) {
            System.err.println("Error optimizing basket with unit price: " + e.getMessage());
        }
    }

    private void handleCreatePriceAlert(Scanner scanner) {
        System.out.print("Enter product name to watch: ");
        String productName = scanner.nextLine();

        List<PriceEntryDTO> priceEntries = priceTrendController.getPriceTrendsForProduct(productName);
        if (priceEntries.isEmpty()) {
            System.out.println("Product not found. Please check the product name and try again.");
            return;
        }

        PriceEntryDTO latestPrice = priceEntries.get(0);
        System.out.printf("Current price for %s: %.2fRON at %s%n",
                productName, latestPrice.getPrice(), latestPrice.getStoreName());

        System.out.print("Enter target price (must be below current price): ");
        double targetPrice;
        try {
            targetPrice = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price format.");
            return;
        }

        String productId = latestPrice.getProductName();

        try {
            PriceAlertDTO alertDTO = priceAlertController.createAlert(DEFAULT_USER_ID, productId, targetPrice);

            System.out.println("Price Alert created");
            System.out.printf("You will be notified when %s reaches %.2fRON or lower.%n",
                    productName, targetPrice);

            System.out.println("Alert ID: " + alertDTO.getId());
        } catch (Exception e) {
            System.err.println("Error creating price alert: " + e.getMessage());
        }
    }

    private void handleViewPriceAlerts(Scanner scanner) {
        try {
            List<PriceAlertDTO> alerts = priceAlertController.getUserAlerts(DEFAULT_USER_ID);

            if (alerts.isEmpty()) {
                System.out.println("No active price alerts found.");
            } else {
                System.out.println("Your price alerts:");

                for (PriceAlertDTO alert : alerts) {
                    if (alert.isActive()) {
                        System.out.printf("ID: %d | %s | Target: %.2fRON | Current: %.2fRON | Store: %s | Created: %s%n",
                                alert.getId(),
                                alert.getProductName(),
                                alert.getTargetPrice(),
                                alert.getCurrentPrice(),
                                alert.getStoreName(),
                                alert.getCreatedAt());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error viewing price alerts: " + e.getMessage());
        }
    }

    private void handleViewTriggeredAlerts(Scanner scanner) {
        try {
            List<PriceAlertDTO> alerts = priceAlertController.getTriggeredAlerts(DEFAULT_USER_ID);

            if (alerts.isEmpty()) {
                System.out.println("No triggered price alerts found.");
            } else {
                System.out.println("Triggered price alerts:");

                for (PriceAlertDTO alert : alerts) {
                    System.out.printf("ID: %d | %s | Target: %.2fRON | Current: %.2fRON | Store: %s | Triggered: %s%n",
                            alert.getId(),
                            alert.getProductName(),
                            alert.getTargetPrice(),
                            alert.getCurrentPrice(),
                            alert.getStoreName(),
                            alert.getTriggeredAt());
                }
            }
        } catch (Exception e) {
            System.err.println("Error viewing triggered alerts: " + e.getMessage());
        }
    }

    private void handleDeletePriceAlert(Scanner scanner) {
        handleViewPriceAlerts(scanner);

        System.out.print("\nEnter the alert ID to delete: ");
        Long alertId;
        try {
            alertId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
            return;
        }

        try {
            priceAlertController.deleteAlert(alertId);
            System.out.println("Price Alert deleted.");
        } catch (Exception e) {
            System.err.println("Error deleting price alert: " + e.getMessage());
        }
    }
}