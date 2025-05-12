package org.example.service;

import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountAnalysisService {

    private final ItemRepository<Discount> discountRepository;

    @Autowired
    public DiscountAnalysisService(ItemRepository<Discount> discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<DiscountDTO> getHighestDiscounts(int limit) {
        LocalDate today = LocalDate.now();
        System.out.println("Fetching highest discounts. Current date: " + today);

        List<Discount> allDiscounts = discountRepository.loadAllEntries();
        System.out.println("All loaded discount entries:");
        allDiscounts.forEach(discount -> System.out.println(" - " + discount));

        List<Discount> validDiscounts = allDiscounts.stream()
                .filter(discount -> {
                    boolean isValid = !today.isBefore(discount.getStartingDate()) &&
                            !today.isAfter(discount.getEndingDate());
                    return isValid;
                })
                .collect(Collectors.toList());

        System.out.println("Filtered valid discounts:");
        validDiscounts.forEach(discount -> System.out.println(" - " + discount));

        List<DiscountDTO> highestDiscounts = validDiscounts.stream()
                .sorted(Comparator.comparing(Discount::getPercentageOfDiscount).reversed())
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        System.out.println("Final highest discounts (as DTOs):");
        highestDiscounts.forEach(dto -> System.out.println(" - " + dto));

        return highestDiscounts;
    }

    public List<DiscountDTO> getNewlyAddedDiscounts() {
        LocalDate today = LocalDate.now();

        return discountRepository.loadAllEntries().stream()
                .filter(discount -> discount.getStartingDate().equals(today))
                .sorted(Comparator.comparing(Discount::getStartingDate).reversed())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DiscountDTO convertToDTO(Discount discount) {
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
    }
}