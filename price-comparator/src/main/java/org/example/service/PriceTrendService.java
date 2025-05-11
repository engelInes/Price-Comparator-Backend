package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceTrendService {

    private final ProductRepository productRepository;

    @Autowired
    public PriceTrendService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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

    public List<PriceEntryDTO> getPriceTrendsForProductAndStore(String productName, String storeName) {

        return productRepository.findByProductNameAndStore(productName, storeName).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }

    public List<PriceEntryDTO> getPriceTrendsByCategory(String category) {
        return productRepository.findByProductCategory(category).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }

    public List<PriceEntryDTO> getPriceTrendsByBrand(String brand) {
        return productRepository.findByBrand(brand).stream()
                .sorted(Comparator.comparing(PriceEntry::getDate))
                .map(PriceEntryDTO::from)
                .collect(Collectors.toList());
    }
}