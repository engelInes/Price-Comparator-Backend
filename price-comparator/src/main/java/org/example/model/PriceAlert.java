package org.example.model;

public class PriceAlert {
    private String productId;
    private double targetPrice;

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

    @Override
    public String toString() {
        return "PriceAlert{" +
                "productId='" + productId + '\'' +
                ", targetPrice=" + targetPrice +
                '}';
    }

}
