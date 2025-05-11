package org.example.dto;

public class BasketItemDTO {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private double unitPrice;
    private String unitPriceLabel;
    private double savings;
    private String storeName;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitPriceLabel() {
        return unitPriceLabel;
    }

    public void setUnitPriceLabel(String unitPriceLabel) {
        this.unitPriceLabel = unitPriceLabel;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}