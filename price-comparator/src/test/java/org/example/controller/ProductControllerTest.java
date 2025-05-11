//import org.example.controller.ProductController;
//import org.example.dto.PriceEntryDTO;
//import org.example.model.PriceEntry;
//import org.example.service.IProductService;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static java.nio.file.Paths.get;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ProductController.class)
//public class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private IProductService productService;
//
//    @Test
//    public void testLoadPriceEntries() throws Exception {
//        PriceEntryDTO entry = new PriceEntryDTO(new PriceEntry("P001", "Detergent", "Ariel", 1, "kg", "Lidl", LocalDate.of(2024, 1, 1), 2.99, "RON");
//        when(productService.loadPriceEntries("test.csv")).thenReturn(List.of(entry));
//
//        mockMvc.perform(get("/api/products/load")
//                        .param("filePath", "test.csv"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].productId").value("P123"));
//    }
//}
