import org.example.dto.DiscountDTO;
import org.example.model.Discount;
import org.example.repository.ItemRepository;
import org.example.service.DiscountService;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscountServiceTest {

    @Test
    public void testLoadDiscounts() {
        ItemRepository<Discount> mockRepo = mock(ItemRepository.class);
        DiscountService service = new DiscountService(mockRepo);

        Discount discount = new Discount();
        discount.setProductId("123");
        discount.setStartingDate(LocalDate.now());
        discount.setEndingDate(LocalDate.now().plusDays(1));
        discount.setPercentageOfDiscount(10.0);

        when(mockRepo.loadEntriesFromFile("test_prices.csv")).thenReturn(List.of(discount));

        List<DiscountDTO> result = service.loadDiscounts("test_prices.csv");

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getProductId());
    }

    @Test
    public void testGetMaxDiscountPerProduct() {
        ItemRepository<Discount> mockRepo = mock(ItemRepository.class);
        DiscountService service = new DiscountService(mockRepo);

        LocalDate today = LocalDate.now();

        Discount d1 = new Discount("1", "Item A", "BrandX", 1, "pc", "Cat",
                today.minusDays(1), today.plusDays(1), 20.0);

        Discount d2 = new Discount("1", "Item A", "BrandX", 1, "pc", "Cat",
                today.minusDays(2), today.plusDays(2), 30.0);

        Discount d3 = new Discount("2", "Item B", "BrandY", 1, "pc", "Cat",
                today.minusDays(1), today.plusDays(1), 25.0);

        when(mockRepo.loadAllEntries()).thenReturn(List.of(d1, d2, d3));

        List<DiscountDTO> result = service.getMaxDiscountPerProduct(10);

        result.sort((a, b) -> Double.compare(b.getPercentageOfDiscount(), a.getPercentageOfDiscount()));

        assertAll("Max discounts per product",
                () -> assertEquals(2, result.size()),
                () -> assertEquals("1", result.get(0).getProductId()),
                () -> assertEquals(30.0, result.get(0).getPercentageOfDiscount(), 0.001),
                () -> assertEquals("2", result.get(1).getProductId()),
                () -> assertEquals(25.0, result.get(1).getPercentageOfDiscount(), 0.001)
        );
    }
}
