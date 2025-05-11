package org.example.repository;

import org.example.model.PriceAlert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PriceAlertRepository {
    private final Map<Long, PriceAlert> alerts = new HashMap<>();
    private long nextId = 1;

    public PriceAlert save(PriceAlert alert) {
        if (alert.getId() == null) {
            alert.setId(nextId++);
            alert.setCreatedAt(LocalDateTime.now());
            alert.setActive(true);
        }
        alerts.put(alert.getId(), alert);
        return alert;
    }

    public PriceAlert findById(Long id) {
        return alerts.get(id);
    }

    public List<PriceAlert> findByUserId(String userId) {
        return alerts.values().stream()
                .filter(alert -> alert.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<PriceAlert> findActiveAlertsByProductId(String productId) {
        return alerts.values().stream()
                .filter(alert -> alert.getProductId().equals(productId) && alert.isActive())
                .collect(Collectors.toList());
    }

    public List<PriceAlert> findAllActive() {
        return alerts.values().stream()
                .filter(PriceAlert::isActive)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        alerts.remove(id);
    }

    public List<PriceAlert> findAll() {
        return new ArrayList<>(alerts.values());
    }
}