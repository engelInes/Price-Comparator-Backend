package org.example.controller;

import org.example.dto.DiscountDTO;
import org.example.service.IDiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiscountController.class)
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDiscountService discountService;

    @Test
    public void testGetMaxDiscountPerProduct() throws Exception {
        DiscountDTO dto = new DiscountDTO();
        dto.setProductId("P001");
        dto.setPercentageOfDiscount(30.0);

        when(discountService.getMaxDiscountPerProduct(5)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/discounts/discounts/top-max-per-product")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P001"))
                .andExpect(jsonPath("$[0].percentageOfDiscount").value(30.0));
    }
}
