package org.example.service;

import org.example.dto.PriceAlertDTO;
import org.example.model.PriceAlert;
import org.example.model.PriceEntry;
import org.example.repository.PriceAlertRepository;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing price alerts.
 * Provides methods to create, retrieve, update, and delete price alerts,
 * as well as to check and trigger alerts based on current prices.
 */
@Service
public class PriceAlertService {

    private final PriceAlertRepository alertRepository;
    private final ProductRepository productRepository;

    /**
     * Constructs a PriceAlertService with the specified repositories.
     *
     * @param alertRepository   the repository to manage price alerts
     * @param productRepository the repository to access product data
     */
    @Autowired
    public PriceAlertService(PriceAlertRepository alertRepository, ProductRepository productRepository) {
        this.alertRepository = alertRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new price alert for a user.
     *
     * @param userId      the ID of the user
     * @param productId   the ID of the product
     * @param targetPrice the target price to trigger the alert
     * @return the created PriceAlertDTO
     */
    public PriceAlertDTO createAlert(String userId, String productId, double targetPrice) {
        PriceAlert alert = new PriceAlert();
        alert.setUserId(userId);
        alert.setProductId(productId);
        alert.setTargetPrice(targetPrice);

        PriceAlert savedAlert = alertRepository.save(alert);
        return convertToDTO(savedAlert);
    }

    /**
     * Retrieves all price alerts for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of PriceAlertDTOs
     */
    public List<PriceAlertDTO> getUserAlerts(String userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a price alert by its ID.
     *
     * @param alertId the ID of the alert to delete
     */
    public void deleteAlert(Long alertId) {
        alertRepository.delete(alertId);
    }

    /**
     * Updates the target price of an existing alert.
     *
     * @param alertId        the ID of the alert to update
     * @param newTargetPrice the new target price
     * @return the updated PriceAlertDTO, or null if the alert does not exist
     */
    public PriceAlertDTO updateAlert(Long alertId, double newTargetPrice) {
        PriceAlert alert = alertRepository.findById(alertId);
        if (alert != null) {
            alert.setTargetPrice(newTargetPrice);
            alert.setActive(true);
            alert.setTriggeredAt(null);
            alertRepository.save(alert);
            return convertToDTO(alert);
        }
        return null;
    }

    /**
     * Checks all active price alerts and triggers them if the current price
     * is less than or equal to the target price.
     * This method is scheduled to run at fixed intervals.
     */
    @Scheduled(fixedRate = 3600000)
    public void checkPriceAlerts() {
        List<PriceAlert> activeAlerts = alertRepository.findAllActive();

        Map<String, List<PriceAlert>> alertsByProduct = activeAlerts.stream()
                .collect(Collectors.groupingBy(PriceAlert::getProductId));

        for (Map.Entry<String, List<PriceAlert>> entry : alertsByProduct.entrySet()) {
            String productId = entry.getKey();
            List<PriceAlert> productAlerts = entry.getValue();

            List<PriceEntry> priceEntries = productRepository.findByProductName(productId);
            if (priceEntries.isEmpty()) continue;

            PriceEntry latestPriceEntry = priceEntries.stream()
                    .max(Comparator.comparing(PriceEntry::getDate))
                    .orElse(null);

            if (latestPriceEntry == null) continue;

            double currentPrice = latestPriceEntry.getPrice();

            for (PriceAlert alert : productAlerts) {
                if (currentPrice <= alert.getTargetPrice()) {
                    alert.setActive(false);
                    alert.setTriggeredAt(LocalDateTime.now());
                    alertRepository.save(alert);
                }
            }
        }
    }

    /**
     * Retrieves all triggered price alerts for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of triggered PriceAlertDTOs
     */
    public List<PriceAlertDTO> getTriggeredAlerts(String userId) {
        return alertRepository.findByUserId(userId).stream()
                .filter(alert -> !alert.isActive() && alert.getTriggeredAt() != null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a PriceAlert entity to its corresponding DTO.
     *
     * @param alert the PriceAlert entity
     * @return the corresponding PriceAlertDTO
     */
    private PriceAlertDTO convertToDTO(PriceAlert alert) {
        PriceAlertDTO dto = new PriceAlertDTO();
        dto.setId(alert.getId());
        dto.setUserId(alert.getUserId());
        dto.setProductId(alert.getProductId());
        dto.setTargetPrice(alert.getTargetPrice());
        dto.setActive(alert.isActive());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setTriggeredAt(alert.getTriggeredAt());

        List<PriceEntry> entries = productRepository.findByProductName(alert.getProductId());
        if (!entries.isEmpty()) {
            PriceEntry latestEntry = entries.stream()
                    .max(Comparator.comparing(PriceEntry::getDate))
                    .orElse(null);

            if (latestEntry != null) {
                dto.setProductName(latestEntry.getProductName());
                dto.setCurrentPrice(latestEntry.getPrice());
                dto.setStoreName(latestEntry.getStoreName());
            }
        }

        return dto;
    }
}