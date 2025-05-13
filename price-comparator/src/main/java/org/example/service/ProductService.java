package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for loading price entries from files.
 * Provides a method to load price entries from a specified CSV file.
 */
@Service
public class ProductService implements IProductService{

    private final ItemRepository<PriceEntry> productRepository;

    /**
     * Constructs a ProductService with the specified ItemRepository.
     *
     * @param productRepository the repository to load price entries
     */
    @Autowired
    public ProductService(ItemRepository<PriceEntry> productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Loads price entries from a CSV file.
     *
     * @param filePath the path to the CSV file
     * @return a list of PriceEntryDTOs
     */
    public List<PriceEntryDTO> loadPriceEntries(String filePath) {
        return productRepository.loadEntriesFromFile(filePath).stream().map(PriceEntryDTO::from).collect(Collectors.toList());
    }
}
