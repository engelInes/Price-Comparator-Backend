package org.example.service;

import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for handling discount operations.
 */
@Service
public class DiscountService implements IDiscountService {
    private final ItemRepository<Discount> discountRepository;

    /**
     * Constructs a new DiscountService with the given discount repository.
     *
     * @param discountRepository The repository for loading discount data.
     */
    @Autowired
    public DiscountService(ItemRepository<Discount> discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * Loads discount entries from a file and converts them to DTOs.
     *
     * @param filePath The file path containing discount data.
     * @return A list of DiscountDTO objects.
     */
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

    /**
     * Retrieves the top discounts per product, limited to a specified number.
     *
     * @param limit The maximum number of discounts to return.
     * @return A list of DiscountDTO objects sorted by discount percentage.
     */
    public List<DiscountDTO> getMaxDiscountPerProduct(int limit) {
        LocalDate today = LocalDate.now();

        List<Discount> allDiscounts = discountRepository.loadAllEntries();
        System.out.println("Total loaded for max-per-product: " + allDiscounts.size());

        Map<String, Discount> maxDiscounts = allDiscounts.stream()
                .filter(discount -> !today.isBefore(discount.getStartingDate()) &&
                        !today.isAfter(discount.getEndingDate()))
                .collect(Collectors.toMap(
                        Discount::getProductId,
                        discount -> discount,
                        (d1, d2) -> d1.getPercentageOfDiscount() >= d2.getPercentageOfDiscount() ? d1 : d2
                ));

        List<DiscountDTO> result = maxDiscounts.values().stream()
                .sorted(Comparator.comparing(Discount::getPercentageOfDiscount).reversed())
                .limit(limit)
                .map(DiscountDTO::convertToDTO)
                .collect(Collectors.toList());

        System.out.println("Max discounts per product selected: " + result.size());
        return result;
    }

}