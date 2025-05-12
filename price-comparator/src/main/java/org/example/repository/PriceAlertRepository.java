package org.example.repository;

import org.example.model.PriceAlert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for handling storage of PriceAlert entities.
 */
@Repository
public class PriceAlertRepository {
    private final Map<Long, PriceAlert> alerts = new HashMap<>();
    private long nextId = 1;

    /**
     * Saves a new or existing price alert.
     *
     * @param alert The price alert to save.
     * @return The saved alert.
     */
    public PriceAlert save(PriceAlert alert) {
        if (alert.getId() == null) {
            alert.setId(nextId++);
            alert.setCreatedAt(LocalDateTime.now());
            alert.setActive(true);
        }
        alerts.put(alert.getId(), alert);
        return alert;
    }

    /**
     * Finds a price alert by its ID.
     *
     * @param id The ID of the alert.
     * @return The alert with the given ID, or null if not found.
     */
    public PriceAlert findById(Long id) {
        return alerts.get(id);
    }

    /**
     * Finds all alerts created by a specific user.
     *
     * @param userId The user ID.
     * @return List of alerts associated with the user.
     */
    public List<PriceAlert> findByUserId(String userId) {
        return alerts.values().stream()
                .filter(alert -> alert.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Finds all active alerts for a specific product.
     *
     * @param productId The product ID.
     * @return List of active alerts for the product.
     */
    public List<PriceAlert> findActiveAlertsByProductId(String productId) {
        return alerts.values().stream()
                .filter(alert -> alert.getProductId().equals(productId) && alert.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Returns all active alerts.
     *
     * @return List of active alerts.
     */
    public List<PriceAlert> findAllActive() {
        return alerts.values().stream()
                .filter(PriceAlert::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Deletes the alert with the given ID.
     *
     * @param id The ID of the alert to delete.
     */
    public void delete(Long id) {
        alerts.remove(id);
    }

    /**
     * Returns all stored alerts.
     *
     * @return List of all alerts.
     */
    public List<PriceAlert> findAll() {
        return new ArrayList<>(alerts.values());
    }
}