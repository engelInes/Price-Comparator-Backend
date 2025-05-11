package org.example.dto;

import java.util.ArrayList;
import java.util.List;

public class OptimizedBasketDTO {
    private List<ShoppingListDTO> shoppingLists;
    private double totalCost;
    private double totalSavings;
    private double originalCost;

    public OptimizedBasketDTO() {
        this.shoppingLists = new ArrayList<>();
    }

    public List<ShoppingListDTO> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingListDTO> shoppingLists) {
        this.shoppingLists = shoppingLists;
        recalculateTotals();
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

    public double getOriginalCost() {
        return originalCost;
    }

    public void setOriginalCost(double originalCost) {
        this.originalCost = originalCost;
    }

    public void addShoppingList(ShoppingListDTO shoppingList) {
        this.shoppingLists.add(shoppingList);
        recalculateTotals();
    }

    private void recalculateTotals() {
        this.totalCost = shoppingLists.stream().mapToDouble(ShoppingListDTO::getTotalCost).sum();
        this.totalSavings = shoppingLists.stream().mapToDouble(ShoppingListDTO::getTotalSavings).sum();
    }
}