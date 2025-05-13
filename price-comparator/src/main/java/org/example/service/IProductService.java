package org.example.service;


import org.example.dto.PriceEntryDTO;

import java.util.List;

/**
 * Service interface for handling product price data.
 */
public interface IProductService {

    /**
     * Loads product price entries from a CSV file.
     *
     * @param filepath The path to the file containing price data.
     */
    List<PriceEntryDTO> loadPriceEntries(String filepath);
}
