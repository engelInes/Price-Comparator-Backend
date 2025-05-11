package org.example.service;

import org.example.dto.PriceAlertDTO;
import org.example.model.PriceAlert;
import org.example.model.PriceEntry;
import org.example.repository.PriceAlertRepository;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class PriceAlertServiceTest {

    private PriceAlertService priceAlertService;
    private PriceAlertRepository alertRepository;
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        alertRepository = mock(PriceAlertRepository.class);
        productRepository = mock(ProductRepository.class);
        priceAlertService = new PriceAlertService(alertRepository, productRepository);
    }

    @Test
    public void testCreateAlert() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId("U1");
        alert.setProductId("P001");
        alert.setTargetPrice(10.0);

        when(alertRepository.save(any(PriceAlert.class))).thenReturn(alert);
        when(productRepository.findByProductName("P001")).thenReturn(List.of());

        PriceAlertDTO result = priceAlertService.createAlert("U1", "P001", 10.0);

        assertEquals("U1", result.getUserId());
        assertEquals("P001", result.getProductId());
        assertEquals(10.0, result.getTargetPrice(), 0.001);
    }

    @Test
    public void testGetUserAlerts() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId("U1");
        alert.setProductId("P001");
        alert.setTargetPrice(15.0);
        alert.setActive(true);

        when(alertRepository.findByUserId("U1")).thenReturn(List.of(alert));
        when(productRepository.findByProductName("P001")).thenReturn(List.of());

        List<PriceAlertDTO> results = priceAlertService.getUserAlerts("U1");
        assertEquals(1, results.size());
        assertEquals("P001", results.get(0).getProductId());
    }

    @Test
    public void testDeleteAlert() {
        priceAlertService.deleteAlert(123L);
        verify(alertRepository, times(1)).delete(123L);
    }

    @Test
    public void testUpdateAlert() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId("U1");
        alert.setProductId("P001");
        alert.setTargetPrice(15.0);

        when(alertRepository.findById(1L)).thenReturn(alert);
        when(productRepository.findByProductName("P001")).thenReturn(List.of());
        when(alertRepository.save(any(PriceAlert.class))).thenReturn(alert);

        PriceAlertDTO result = priceAlertService.updateAlert(1L, 8.0);

        assertEquals(8.0, result.getTargetPrice(), 0.001);
        assertTrue(result.isActive());
        assertNull(result.getTriggeredAt());
    }

    @Test
    public void testGetTriggeredAlerts() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId("U1");
        alert.setProductId("P001");
        alert.setTargetPrice(5.0);
        alert.setActive(false);
        alert.setTriggeredAt(LocalDateTime.now());

        when(alertRepository.findByUserId("U1")).thenReturn(List.of(alert));
        when(productRepository.findByProductName("P001")).thenReturn(List.of());

        List<PriceAlertDTO> results = priceAlertService.getTriggeredAlerts("U1");

        assertEquals(1, results.size());
        assertEquals("P001", results.get(0).getProductId());
    }

    @Test
    public void testCheckPriceAlerts() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId("U1");
        alert.setProductId("P001");
        alert.setTargetPrice(9.0);
        alert.setActive(true);

        PriceEntry priceEntry = new PriceEntry("P001", "Detergent", "Curatenie", "Ariel", 1, "kg", "Lidl", LocalDate.now(), 7.0, "RON");

        when(alertRepository.findAllActive()).thenReturn(List.of(alert));
        when(productRepository.findByProductName("P001")).thenReturn(List.of(priceEntry));
        when(alertRepository.save(any(PriceAlert.class))).thenReturn(alert);

        priceAlertService.checkPriceAlerts();

        verify(alertRepository).save(argThat(a -> !a.isActive() && a.getTriggeredAt() != null));
    }
}
