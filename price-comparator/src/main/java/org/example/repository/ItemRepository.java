package org.example.repository;

import java.util.List;

public interface ItemRepository<ItemType> {
    List<ItemType> loadEntriesFromFile(String filePath);
    List<ItemType> loadAllEntries();
}
