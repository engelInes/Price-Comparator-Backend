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
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            List<PriceEntry> priceEntries = productRepository.findByProductName(productId);
            if (priceEntries.isEmpty()) continue;

            List<Discount> productDiscounts = activeDiscounts.stream()
                    .filter(d -> d.getProductId().equals(productId))
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
                itemDTO.setProductId(productId);
                itemDTO.setProductName(item.getProductName());
                itemDTO.setQuantity(quantity);
                itemDTO.setPrice(effectivePrice);
                itemDTO.setSavings(discount * quantity);
                itemDTO.setStoreName(store);

                if (priceEntry.getPackageQuantity() > 0) {
                    double unitPrice = effectivePrice / priceEntry.getPackageQuantity();
                    itemDTO.setUnitPrice(unitPrice);
                    itemDTO.setUnitPriceLabel("per " + priceEntry.getPackageUnit());
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

    private double calculateOriginalCost(List<BasketItem> basket) {
        Map<String, Integer> productQuantities = basket.stream()
                .collect(Collectors.toMap(BasketItem::getProductId, BasketItem::getQuantity));

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
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            List<PriceEntry> priceEntries = productRepository.findByProductName(productId);
            if (priceEntries.isEmpty()) continue;

            List<Discount> productDiscounts = activeDiscounts.stream()
                    .filter(d -> d.getProductId().equals(productId))
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
                itemDTO.setProductId(productId);
                itemDTO.setProductName(item.getProductName());
                itemDTO.setQuantity(quantity);
                itemDTO.setPrice(effectivePrice);
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