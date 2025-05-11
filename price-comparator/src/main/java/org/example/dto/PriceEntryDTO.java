package org.example.dto;

import java.time.LocalDate;

public class PriceEntryDTO {
    private String productId;
    private String storeName;
    private LocalDate date;
    private double price;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
