package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for retrieving and analyzing price trends of products.
 * Provides methods to fetch price trends based on product name, store, category, or brand.
 */
@Service
public class PriceTrendService {

    private final ProductRepository productRepository;

    /**
     * Constructs a PriceTrendService with the specified ProductRepository.
     *
     * @param productRepository the repository to access product data
     */
    @Autowired
    public PriceTrendService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves the price trend for a specific product.
     *
     * @param productName the name of the product
     * @return a list of PriceEntryDTOs sorted by date
     */
    public List<PriceEntryDTO> getPriceTrendsForProduct(String productName) {
        List<PriceEntry> entries = productRepository.findByProductName(productName);

        if (entries.isEmpty()) {
            System.out.println("No entries found for product ID: " + productName);
        } else {
            System.out.println("Found entries for product ID: " + productName);
            for (PriceEntry entry : entries) {
                System.out.println(" - " + entry);
            }
        }

        return entries.stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the price trend for a specific product in a specific store.
     *
     * @param productName the name of the product
     * @param storeName   the name of the store
     * @return a list of PriceEntryDTOs sorted by date
     */
    public List<PriceEntryDTO> getPriceTrendsForProductAndStore(String productName, String storeName) {

        return productRepository.findByProductNameAndStore(productName, storeName).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the price trends for products in a specific category.
     *
     * @param category the category of products
     * @return a list of PriceEntryDTOs sorted by date
     */
    public List<PriceEntryDTO> getPriceTrendsByCategory(String category) {
        return productRepository.findByProductCategory(category).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the price trends for products of a specific brand.
     *
     * @param brand the brand of products
     * @return a list of PriceEntryDTOs sorted by date
     */
    public List<PriceEntryDTO> getPriceTrendsByBrand(String brand) {
        return productRepository.findByBrand(brand).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }
}