package org.example.controller;


import org.example.dto.PriceAlertDTO;
import org.example.service.PriceAlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceAlertController.class)
public class PriceAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceAlertService priceAlertService;

    private PriceAlertDTO buildSampleDTO() {
        PriceAlertDTO dto = new PriceAlertDTO();
        dto.setId(1L);
        dto.setUserId("U1");
        dto.setProductId("P001");
        dto.setTargetPrice(10.0);
        dto.setActive(true);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setProductName("Paine");
        dto.setCurrentPrice(9.5);
        dto.setStoreName("Lidl");
        return dto;
    }

    @Test
    public void testCreateAlert() throws Exception {
        PriceAlertDTO dto = buildSampleDTO();
        Mockito.when(priceAlertService.createAlert("U1", "P001", 10.0)).thenReturn(dto);

        mockMvc.perform(post("/api/price-alerts")
                        .param("userId", "U1")
                        .param("productId", "P001")
                        .param("targetPrice", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("U1"))
                .andExpect(jsonPath("$.productId").value("P001"))
                .andExpect(jsonPath("$.targetPrice").value(10.0));
    }

    @Test
    public void testGetUserAlerts() throws Exception {
        PriceAlertDTO dto = buildSampleDTO();
        Mockito.when(priceAlertService.getUserAlerts("U1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/price-alerts/user/U1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P001"));
    }

    @Test
    public void testGetTriggeredAlerts() throws Exception {
        PriceAlertDTO dto = buildSampleDTO();
        dto.setActive(false);
        dto.setTriggeredAt(LocalDateTime.now());
        Mockito.when(priceAlertService.getTriggeredAlerts("U1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/price-alerts/triggered/U1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(false));
    }

    @Test
    public void testUpdateAlertSuccess() throws Exception {
        PriceAlertDTO dto = buildSampleDTO();
        dto.setTargetPrice(8.0);
        Mockito.when(priceAlertService.updateAlert(eq(1L), eq(8.0))).thenReturn(dto);

        mockMvc.perform(put("/api/price-alerts/1")
                        .param("newTargetPrice", "8.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetPrice").value(8.0));
    }

    @Test
    public void testUpdateAlertNotFound() throws Exception {
        Mockito.when(priceAlertService.updateAlert(eq(999L), eq(20.0))).thenReturn(null);

        mockMvc.perform(put("/api/price-alerts/999")
                        .param("newTargetPrice", "20.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAlert() throws Exception {
        mockMvc.perform(delete("/api/price-alerts/1"))
                .andExpect(status().isNoContent());
    }
}
