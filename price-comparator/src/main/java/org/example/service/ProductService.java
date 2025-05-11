package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.example.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ItemRepository<PriceEntry> productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public List<PriceEntryDTO> loadPriceEntries(String filePath) {
        List<PriceEntry> entries = productRepository.loadEntriesFromFile(filePath);

        return entries.stream().map(entry -> {
            PriceEntryDTO dto = new PriceEntryDTO();
            dto.setProductId(entry.getProductId());
            dto.setStoreName(entry.getStoreName());
            dto.setDate(entry.getDate());
            dto.setPrice(entry.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }
}
