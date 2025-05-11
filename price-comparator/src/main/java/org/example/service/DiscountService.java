package org.example.service;

import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.DiscountRepository;
import org.example.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

public class DiscountService {
    private final ItemRepository<Discount> discountRepository;

    public DiscountService() {
        this.discountRepository = new DiscountRepository();
    }

    public List<DiscountDTO> loadDiscounts(String filePath) {
        List<Discount> discounts = discountRepository.loadEntriesFromFile(filePath);

        return discounts.stream().map(discount -> {
            DiscountDTO dto = new DiscountDTO();
            dto.setProductId(discount.getProductId());
            dto.setProductName(discount.getProductName());
            dto.setBrand(discount.getBrand());
            dto.setPackageQuantity(discount.getPackageQuantity());
            dto.setPackageUnit(discount.getPackageUnit());
            dto.setProductCategory(discount.getProductCategory());
            dto.setStartingDate(discount.getStartingDate());
            dto.setEndingDate(discount.getEndingDate());
            dto.setPercentageOfDiscount(discount.getPercentageOfDiscount());
            return dto;
        }).collect(Collectors.toList());
    }
}