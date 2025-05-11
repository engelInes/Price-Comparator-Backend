package org.example.service;


import org.example.dto.PriceEntryDTO;

import java.util.List;

public interface IProductService {
    List<PriceEntryDTO> loadPriceEntries(String filepath);
}
