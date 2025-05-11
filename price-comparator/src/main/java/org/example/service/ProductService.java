package org.example.service;

import org.example.dto.PriceEntryDTO;
import org.example.model.Discount;
import org.example.model.PriceEntry;
import org.example.repository.ItemRepository;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService{

    private final ItemRepository<PriceEntry> productRepository;

    @Autowired
    public ProductService(ItemRepository<PriceEntry> productRepository) {
        this.productRepository = productRepository;
    }

    public List<PriceEntryDTO> loadPriceEntries(String filePath) {
        return productRepository.loadEntriesFromFile(filePath).stream().map(PriceEntryDTO::from).collect(Collectors.toList());
    }
}
