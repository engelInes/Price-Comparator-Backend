package org.example.controller;

import org.example.dto.PriceAlertDTO;
import org.example.service.PriceAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-alerts")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;

    @Autowired
    public PriceAlertController(PriceAlertService priceAlertService) {
        this.priceAlertService = priceAlertService;
    }

    @PostMapping
    public PriceAlertDTO createAlert(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam double targetPrice) {
        return priceAlertService.createAlert(userId, productId, targetPrice);
    }

    @GetMapping("/user/{userId}")
    public List<PriceAlertDTO> getUserAlerts(@PathVariable String userId) {
        return priceAlertService.getUserAlerts(userId);
    }

    @GetMapping("/triggered/{userId}")
    public List<PriceAlertDTO> getTriggeredAlerts(@PathVariable String userId) {
        return priceAlertService.getTriggeredAlerts(userId);
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<PriceAlertDTO> updateAlert(
            @PathVariable Long alertId,
            @RequestParam double newTargetPrice) {
        PriceAlertDTO updatedAlert = priceAlertService.updateAlert(alertId, newTargetPrice);
        if (updatedAlert != null) {
            return ResponseEntity.ok(updatedAlert);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        priceAlertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}