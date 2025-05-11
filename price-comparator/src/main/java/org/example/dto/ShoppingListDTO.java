package org.example.dto;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListDTO {
    private String storeName;
    private List<BasketItemDTO> items;
    private double totalCost;
    private double totalSavings;

    public ShoppingListDTO() {
        this.items = new ArrayList<>();
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<BasketItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BasketItemDTO> items) {
        this.items = items;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(double totalSavings) {
        this.totalSavings = totalSavings;
    }

    public void addItem(BasketItemDTO item) {
        items.add(item);
        recalculateTotals();
    }

    private void recalculateTotals() {
        this.totalCost = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        this.totalSavings = items.stream().mapToDouble(BasketItemDTO::getSavings).sum();
    }
}