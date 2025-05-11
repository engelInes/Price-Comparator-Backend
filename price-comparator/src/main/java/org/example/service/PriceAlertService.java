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

@Service
public class PriceAlertService {

    private final PriceAlertRepository alertRepository;
    private final ProductRepository productRepository;

    @Autowired
    public PriceAlertService(PriceAlertRepository alertRepository, ProductRepository productRepository) {
        this.alertRepository = alertRepository;
        this.productRepository = productRepository;
    }

    public PriceAlertDTO createAlert(String userId, String productId, double targetPrice) {
        PriceAlert alert = new PriceAlert();
        alert.setUserId(userId);
        alert.setProductId(productId);
        alert.setTargetPrice(targetPrice);

        PriceAlert savedAlert = alertRepository.save(alert);
        return convertToDTO(savedAlert);
    }

    public List<PriceAlertDTO> getUserAlerts(String userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteAlert(Long alertId) {
        alertRepository.delete(alertId);
    }

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

    public List<PriceAlertDTO> getTriggeredAlerts(String userId) {
        return alertRepository.findByUserId(userId).stream()
                .filter(alert -> !alert.isActive() && alert.getTriggeredAt() != null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

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