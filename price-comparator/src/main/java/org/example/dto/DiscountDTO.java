package org.example.dto;

import org.example.model.Discount;

import java.time.LocalDate;

public class DiscountDTO {
    private String productId;
    private String productName;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String productCategory;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private double percentageOfDiscount;

    public DiscountDTO() {};

    public DiscountDTO(String productId, String productName, String brand, double packageQuantity, String packageUnit, String productCategory, LocalDate startingDate, LocalDate endingDate, double percentageOfDiscount) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.packageQuantity = packageQuantity;
        this.packageUnit = packageUnit;
        this.productCategory = productCategory;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.percentageOfDiscount = percentageOfDiscount;

    }
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(double packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public double getPercentageOfDiscount() {
        return percentageOfDiscount;
    }

    public void setPercentageOfDiscount(double percentageOfDiscount) {
        this.percentageOfDiscount = percentageOfDiscount;
    }

    public static DiscountDTO convertToDTO(Discount discount) {
        return new DiscountDTO(
                discount.getProductId(),
                discount.getProductName(),
                discount.getBrand(),
                discount.getPackageQuantity(),
                discount.getPackageUnit(),
                discount.getProductCategory(),
                discount.getStartingDate(),
                discount.getEndingDate(),
                discount.getPercentageOfDiscount()
        );
    }
}
