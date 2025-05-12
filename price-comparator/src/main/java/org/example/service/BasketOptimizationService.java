package org.example.service;

import org.example.dto.BasketItemDTO;
import org.example.dto.OptimizedBasketDTO;
import org.example.dto.ShoppingListDTO;
import org.example.model.BasketItem;
import org.example.model.Discount;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasketOptimizationService {

    private final ProductRepository productRepository;
    private final ItemRepository<Discount> discountRepository;

    @Autowired
    public BasketOptimizationService(ProductRepository productRepository, ItemRepository<Discount> discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    /**
     * Optimizes basket by finding the lowest price for each item from all stores
     * @param basket List of basket items to optimize
     * @return OptimizedBasketDTO with the most cost-effective shopping strategy
     */
    public OptimizedBasketDTO optimizeBasket(List<BasketItem> basket) {

        OptimizedBasketDTO result = new OptimizedBasketDTO();
        LocalDate today = LocalDate.now();

        double originalCost = calculateOriginalCost(basket);
        result.setOriginalCost(originalCost);

        List<Discount> activeDiscounts = discountRepository.loadAllEntries().stream()
                .filter(d -> !today.isBefore(d.getStartingDate()) && !today.isAfter(d.getEndingDate()))
                .collect(Collectors.toList());

        Set<String> allStores = productRepository.loadAllEntries().stream()
                .map(PriceEntry::getStoreName)
                .collect(Collectors.toSet());

        Map<String, ShoppingListDTO> storeShoppingLists = new HashMap<>();
        for (String store : allStores) {
            storeShoppingLists.put(store, new ShoppingListDTO());
            storeShoppingLists.get(store).setStoreName(store);
        }

        for (BasketItem item : basket) {
            String productName = item.getProductName();
            int quantity = item.getQuantity();

            List<PriceEntry> priceEntries = productRepository.findByProductName(productName);
            if (priceEntries.isEmpty()) continue;

            List<Discount> productDiscounts = activeDiscounts.stream()
                    .filter(d -> d.getProductName().equals(productName))
                    .collect(Collectors.toList());

            Map<String, Double> storeToEffectivePrice = new HashMap<>();
            Map<String, Double> storeToDiscount = new HashMap<>();
            Map<String, PriceEntry> storeToPriceEntry = new HashMap<>();

            for (PriceEntry entry : priceEntries) {
                String store = entry.getStoreName();
                double regularPrice = entry.getPrice();

                double bestDiscountPercentage = productDiscounts.stream()
                        .mapToDouble(Discount::getPercentageOfDiscount)
                        .max()
                        .orElse(0.0);

                double discountAmount = regularPrice * (bestDiscountPercentage / 100.0);
                double effectivePrice = regularPrice - discountAmount;

                System.out.printf("Product: %s, Store: %s, Regular: %.2f€, Discount: %.2f€, Effective: %.2f€%n",
                        productName, entry.getStoreName(), regularPrice, discountAmount, effectivePrice);

                storeToEffectivePrice.put(store, effectivePrice);
                storeToDiscount.put(store, discountAmount);
                storeToPriceEntry.put(store, entry);
            }

            Map.Entry<String, Double> bestStore = storeToEffectivePrice.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);

            if (bestStore != null) {
                String store = bestStore.getKey();
                double effectivePrice = bestStore.getValue();
                double discount = storeToDiscount.get(store);
                PriceEntry priceEntry = storeToPriceEntry.get(store);

                BasketItemDTO itemDTO = new BasketItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setProductName(productName);
                itemDTO.setQuantity(quantity);
                itemDTO.setPrice(effectivePrice*quantity);
                itemDTO.setSavings(discount * quantity);
                itemDTO.setStoreName(store);

                storeShoppingLists.get(store).addItem(itemDTO);
            }
        }

        List<ShoppingListDTO> nonEmptyLists = storeShoppingLists.values().stream()
                .filter(list -> !list.getItems().isEmpty())
                .collect(Collectors.toList());

        result.setShoppingLists(nonEmptyLists);

        return result;
    }

    public double calculateOriginalCost(List<BasketItem> basket) {
        Map<String, Integer> productQuantities = basket.stream()
                .collect(Collectors.toMap(BasketItem::getProductName, BasketItem::getQuantity));

        Map<String, Double> storeTotalCosts = new HashMap<>();

        Set<String> allStores = productRepository.loadAllEntries().stream()
                .map(PriceEntry::getStoreName)
                .collect(Collectors.toSet());

        for (String store : allStores) {
            double totalCost = 0.0;
            boolean hasAllItems = true;

            for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
                String productName = entry.getKey();
                int quantity = entry.getValue();

                List<PriceEntry> storeEntries = productRepository.findByProductNameAndStore(productName, store);
                if (storeEntries.isEmpty()) {
                    hasAllItems = false;
                    break;
                }

                PriceEntry priceEntry = storeEntries.stream()
                        .max(Comparator.comparing(PriceEntry::getDate))
                        .orElse(null);

                if (priceEntry != null) {
                    totalCost += priceEntry.getPrice() * quantity;
                } else {
                    hasAllItems = false;
                    break;
                }
            }

            if (hasAllItems) {
                storeTotalCosts.put(store, totalCost);
            }
        }

        return storeTotalCosts.values().stream()
                .min(Double::compare)
                .orElse(0.0);
    }

    /**
     * Optimizes basket by finding the lowest unit price for each item from all stores
     * @param basket List of basket items to optimize
     * @return OptimizedBasketDTO with the most cost-effective shopping strategy based on unit prices
     */
    public OptimizedBasketDTO optimizeBasketWithUnitPrice(List<BasketItem> basket) {

        OptimizedBasketDTO result = new OptimizedBasketDTO();
        LocalDate today = LocalDate.now();

        double originalCost = calculateOriginalCost(basket);
        result.setOriginalCost(originalCost);

        List<Discount> activeDiscounts = discountRepository.loadAllEntries().stream()
                .filter(d -> !today.isBefore(d.getStartingDate()) && !today.isAfter(d.getEndingDate()))
                .collect(Collectors.toList());

        Set<String> allStores = productRepository.loadAllEntries().stream()
                .map(PriceEntry::getStoreName)
                .collect(Collectors.toSet());

        Map<String, ShoppingListDTO> storeShoppingLists = new HashMap<>();
        for (String store : allStores) {
            storeShoppingLists.put(store, new ShoppingListDTO());
            storeShoppingLists.get(store).setStoreName(store);
        }

        for (BasketItem item : basket) {
            String productName = item.getProductName();
            int quantity = item.getQuantity();

            List<PriceEntry> priceEntries = productRepository.findByProductName(productName);
            if (priceEntries.isEmpty()) continue;

            List<Discount> productDiscounts = activeDiscounts.stream()
                    .filter(d -> d.getProductName().equals(productName))
                    .collect(Collectors.toList());

            Map<String, Double> storeToUnitPrice = new HashMap<>();
            Map<String, Double> storeToEffectivePrice = new HashMap<>();
            Map<String, Double> storeToDiscount = new HashMap<>();
            Map<String, PriceEntry> storeToPriceEntry = new HashMap<>();

            for (PriceEntry entry : priceEntries) {
                String store = entry.getStoreName();
                double regularPrice = entry.getPrice();
                double packageQuantity = entry.getPackageQuantity();
                String packageUnit = entry.getPackageUnit();

                double unitPrice = packageQuantity > 0 ? regularPrice / packageQuantity : regularPrice;

                double bestDiscountPercentage = productDiscounts.stream()
                        .mapToDouble(Discount::getPercentageOfDiscount)
                        .max()
                        .orElse(0.0);

                double discountAmount = regularPrice * (bestDiscountPercentage / 100.0);
                double effectivePrice = regularPrice - discountAmount;
                double effectiveUnitPrice = packageQuantity > 0 ? effectivePrice / packageQuantity : effectivePrice;

                System.out.printf("Product: %s, Store: %s, Regular: %.2f€, PkgQty: %.2f %s, Unit: %.2f€, Discount: %.2f€, EffectiveUnit: %.2f€%n",
                        productName, entry.getStoreName(), regularPrice, packageQuantity, packageUnit, unitPrice, discountAmount, effectiveUnitPrice);

                storeToUnitPrice.put(store, unitPrice);
                storeToEffectivePrice.put(store, effectivePrice);
                storeToDiscount.put(store, discountAmount);
                storeToPriceEntry.put(store, entry);
            }

            Map.Entry<String, Double> bestStore = storeToEffectivePrice.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);

            if (bestStore != null) {
                String store = bestStore.getKey();
                double effectivePrice = bestStore.getValue();
                double discount = storeToDiscount.get(store);
                PriceEntry priceEntry = storeToPriceEntry.get(store);

                BasketItemDTO itemDTO = new BasketItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setProductName(productName);
                itemDTO.setQuantity(quantity);
                itemDTO.setPrice(effectivePrice * quantity);
                itemDTO.setSavings(discount * quantity);
                itemDTO.setStoreName(store);

                if (priceEntry.getPackageQuantity() > 0) {
                    double unitPrice = effectivePrice / priceEntry.getPackageQuantity();
                    itemDTO.setUnitPrice(unitPrice);
                    itemDTO.setUnitPriceLabel("per " + priceEntry.getPackageUnit());
                } else {
                    itemDTO.setUnitPrice(0);
                    itemDTO.setUnitPriceLabel("N/A");
                }

                storeShoppingLists.get(store).addItem(itemDTO);
            }
        }

        List<ShoppingListDTO> nonEmptyLists = storeShoppingLists.values().stream()
                .filter(list -> !list.getItems().isEmpty())
                .collect(Collectors.toList());

        result.setShoppingLists(nonEmptyLists);

        return result;
    }

}