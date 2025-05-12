package org.example.repository;

import java.util.List;

/**
 * A generic repository interface for loading entries from files.
 *
 * @param <ItemType> The type of item to load
 */
public interface ItemRepository<ItemType> {
    /**
     * Loads a list of entries from the specified CSV file.
     *
     * @param filePath The path to the CSV file.
     * @return A list of parsed entries.
     */
    List<ItemType> loadEntriesFromFile(String filePath);

    /**
     * Loads all entries available in the data directory.
     *
     * @return A list of all entries.
     */
    List<ItemType> loadAllEntries();
}
