package org.example;

import org.example.dto.*;
import org.example.model.BasketItem;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final IProductService productService;
    private final PriceTrendService priceTrendService;
    private final DiscountAnalysisService  discountAnalysisService;
    private final DiscountService discountService;
    private final BasketOptimizationService basketOptimizationService;
    private final PriceAlertService priceAlertService;

    private final ConcurrentMap<String, WatchService> fileWatchers = new ConcurrentHashMap<>();

    private static final String DEFAULT_USER_ID = "user1";
    private static final String CSV_DIRECTORY = "src/main/resources/data";

    @Autowired
    public Main(
                IProductService productService,
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

        priceAlertService.checkPriceAlerts();

        List<PriceAlertDTO> triggeredAlerts = priceAlertService.getTriggeredAlerts(DEFAULT_USER_ID);

        if (!triggeredAlerts.isEmpty()) {
            System.out.println("PRICE ALERT NOTIFICATION");
            System.out.println("--------------------------------");
            for (PriceAlertDTO alert : triggeredAlerts) {
                System.out.printf("PRICE DROP ALERT: %s is now at %.2f€ (below your target of %.2f€)!%n",
                        alert.getProductName(), alert.getCurrentPrice(), alert.getTargetPrice());
                System.out.printf("Available at: %s%n", alert.getStoreName());
                System.out.println("--------------------------------");
            }
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
        List<PriceEntryDTO> trend = priceTrendService.getPriceTrendsForProduct(productName);
        if (trend.isEmpty()) {
            System.out.println("No trend data found.");
        } else {
            trend.forEach(e ->
                    System.out.printf("%s: %.2f€ (%s)%n", e.getProductName(), e.getPrice(), e.getDate()));
        }
    }

    private void handleProductStoreTrend(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter store name: ");
        String store = scanner.nextLine();

        List<PriceEntryDTO> trend = priceTrendService.getPriceTrendsForProductAndStore(productName, store);
        if (trend.isEmpty()) {
            System.out.println("No trend data found for that product in the specified store.");
        } else {
            trend.forEach(e ->
                    System.out.printf("%s: %.2f€ (%s) in %s%n", e.getProductName(), e.getPrice(), e.getDate(), e.getStoreName()));
        }
    }

    private void handleCategoryTrend(Scanner scanner) {
        System.out.print("Enter product category: ");
        String category = scanner.nextLine();

        List<PriceEntryDTO> trend = priceTrendService.getPriceTrendsByCategory(category);
        if (trend.isEmpty()) {
            System.out.println("No trend data found for that category.");
        } else {
            trend.forEach(e ->
                    System.out.printf("%s: %.2f€ (%s) [%s]%n", e.getProductName(), e.getPrice(), e.getDate(), e.getStoreName()));
        }
    }

    private void handleBrandTrend(Scanner scanner) {
        System.out.print("Enter brand name: ");
        String brand = scanner.nextLine();

        List<PriceEntryDTO> trend = priceTrendService.getPriceTrendsByBrand(brand);
        if (trend.isEmpty()) {
            System.out.println("No trend data found for that brand.");
        } else {
            trend.forEach(e ->
                    System.out.printf("%s: %.2f€ (%s) [%s]%n", e.getProductId(), e.getPrice(), e.getDate(), e.getStoreName()));
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

        var topDiscounts = discountAnalysisService.getHighestDiscounts(limit);
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

        var maxDiscounts = discountService.getMaxDiscountPerProduct(limit);
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

            List<PriceEntryDTO> priceEntries = priceTrendService.getPriceTrendsForProduct(name);
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

        OptimizedBasketDTO optimized = basketOptimizationService.optimizeBasket(basket);

        System.out.printf("\nOriginal Cost: %.2f€%n", optimized.getOriginalCost());
        System.out.printf("Optimized Cost: %.2f€%n", optimized.getTotalCost());
        System.out.printf("Total Savings: %.2f€%n", optimized.getTotalSavings());

        for (ShoppingListDTO list : optimized.getShoppingLists()) {
            System.out.println("\nStore: " + list.getStoreName());
            for (BasketItemDTO item : list.getItems()) {
                System.out.printf(" - %s: %.2f€ (Qty: %d, Saved: %.2f€)%n",
                        item.getProductName(), item.getPrice(), item.getQuantity(), item.getSavings());
            }
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

            List<PriceEntryDTO> priceEntries = priceTrendService.getPriceTrendsForProduct(name);
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

        OptimizedBasketDTO optimized = basketOptimizationService.optimizeBasketWithUnitPrice(basket);

        System.out.printf("\nOriginal Cost: %.2f€%n", optimized.getOriginalCost());
        System.out.printf("Optimized Cost: %.2f€%n", optimized.getTotalCost());
        System.out.printf("Total Savings: %.2f€%n", optimized.getTotalSavings());

        for (ShoppingListDTO list : optimized.getShoppingLists()) {
            System.out.println("\nStore: " + list.getStoreName());
            for (BasketItemDTO item : list.getItems()) {
                System.out.printf(" - %s: %.2f€ (Qty: %d, Saved: %.2f€) %s %.2f€ per unit%n",
                        item.getProductName(), item.getPrice(), item.getQuantity(), item.getSavings(),
                        item.getUnitPriceLabel(), item.getUnitPrice());
            }
        }
    }

    private void handleCreatePriceAlert(Scanner scanner) {
        System.out.print("Enter product name to watch: ");
        String productName = scanner.nextLine();

        List<PriceEntryDTO> priceEntries = priceTrendService.getPriceTrendsForProduct(productName);
        if (priceEntries.isEmpty()) {
            System.out.println("Product not found. Please check the product name and try again.");
            return;
        }

        PriceEntryDTO latestPrice = priceEntries.get(0);
        System.out.printf("Current price for %s: %.2f€ at %s%n",
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

        PriceAlertDTO alertDTO = priceAlertService.createAlert(DEFAULT_USER_ID, productId, targetPrice);

        System.out.println("Price Alert created");
        System.out.printf("You will be notified when %s reaches %.2f€ or lower.%n",
                productName, targetPrice);

        System.out.println("Alert ID: " + alertDTO.getId());
    }

    private void handleViewPriceAlerts(Scanner scanner) {
        List<PriceAlertDTO> alerts = priceAlertService.getUserAlerts(DEFAULT_USER_ID);

        if (alerts.isEmpty()) {
            System.out.println("No active price alerts found.");
        } else {
            System.out.println("Your price alerts:");

            for (PriceAlertDTO alert : alerts) {
                if (alert.isActive()) {
                    System.out.printf("ID: %d | %s | Target: %.2f€ | Current: %.2f€ | Store: %s | Created: %s%n",
                            alert.getId(),
                            alert.getProductName(),
                            alert.getTargetPrice(),
                            alert.getCurrentPrice(),
                            alert.getStoreName(),
                            alert.getCreatedAt());
                }
            }
        }
    }

    private void handleViewTriggeredAlerts(Scanner scanner) {
        List<PriceAlertDTO> alerts = priceAlertService.getTriggeredAlerts(DEFAULT_USER_ID);

        if (alerts.isEmpty()) {
            System.out.println("No triggered price alerts found.");
        } else {
            System.out.println("Triggered price alerts:");

            for (PriceAlertDTO alert : alerts) {
                System.out.printf("ID: %d | %s | Target: %.2f€ | Current: %.2f€ | Store: %s | Triggered: %s%n",
                        alert.getId(),
                        alert.getProductName(),
                        alert.getTargetPrice(),
                        alert.getCurrentPrice(),
                        alert.getStoreName(),
                        alert.getTriggeredAt());
            }
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

        priceAlertService.deleteAlert(alertId);
        System.out.println("Price Alert deleted.");
    }

}