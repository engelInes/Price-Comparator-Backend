package org.example.controller;

import org.example.dto.DiscountDTO;
import org.example.service.DiscountAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DiscountAnalysisControllerTest {

    @Mock
    private DiscountAnalysisService discountAnalysisService;

    @InjectMocks
    private DiscountAnalysisController discountAnalysisController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(discountAnalysisController).build();
    }

    @Test
    public void testGetHighestDiscounts() throws Exception {
        DiscountDTO dto1 = new DiscountDTO();
        dto1.setProductId("P001");
        dto1.setPercentageOfDiscount(30.0);

        DiscountDTO dto2 = new DiscountDTO();
        dto2.setProductId("P002");
        dto2.setPercentageOfDiscount(20.0);

        when(discountAnalysisService.getHighestDiscounts(anyInt())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/discount-analysis/highest")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P001"))
                .andExpect(jsonPath("$[1].productId").value("P002"));
    }

    @Test
    public void testGetNewlyAddedDiscounts() throws Exception {
        DiscountDTO dto1 = new DiscountDTO();
        dto1.setProductId("P001");

        DiscountDTO dto2 = new DiscountDTO();
        dto2.setProductId("P002");

        when(discountAnalysisService.getNewlyAddedDiscounts()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/discount-analysis/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P001"))
                .andExpect(jsonPath("$[1].productId").value("P002"));
    }
}