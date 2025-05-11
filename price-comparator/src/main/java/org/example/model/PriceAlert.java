package org.example.model;

import java.time.LocalDateTime;

public class PriceAlert {
    private Long id;
    private String userId;
    private String productId;
    private double targetPrice;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime triggeredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }
}