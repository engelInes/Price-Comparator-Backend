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

/**
 * Service for analyzing discount data.
 */
@Service
public class DiscountAnalysisService {

    private final ItemRepository<Discount> discountRepository;

    /**
     * Constructs a new {@code DiscountAnalysisService} with the provided discount repository.
     *
     * @param discountRepository Repository for accessing discount data.
     */
    @Autowired
    public DiscountAnalysisService(ItemRepository<Discount> discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * Retrieves the top active discounts by percentage.
     *
     * @param limit The maximum number of top discounts to return.
     * @return A list of DiscountDTO representing the highest discounts.
     */
    public List<DiscountDTO> getHighestDiscounts(int limit) {
        LocalDate today = LocalDate.now();

        List<Discount> allDiscounts = discountRepository.loadAllEntries();

        List<Discount> validDiscounts = allDiscounts.stream()
                .filter(discount -> {
                    boolean isValid = !today.isBefore(discount.getStartingDate()) &&
                            !today.isAfter(discount.getEndingDate());
                    return isValid;
                })
                .collect(Collectors.toList());

        List<DiscountDTO> highestDiscounts = validDiscounts.stream()
                .sorted(Comparator.comparing(Discount::getPercentageOfDiscount).reversed())
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return highestDiscounts;
    }

    /**
     * Retrieves discounts that were newly added today.
     *
     * @return A list of DiscountDTO for discounts starting today.
     */
    public List<DiscountDTO> getNewlyAddedDiscounts() {
        LocalDate today = LocalDate.now();

        return discountRepository.loadAllEntries().stream()
                .filter(discount -> discount.getStartingDate().equals(today))
                .sorted(Comparator.comparing(Discount::getStartingDate).reversed())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Discount object into a DiscountDTO.
     *
     * @param discount The discount entity to convert.
     * @return A DiscountDTO object.
     */
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