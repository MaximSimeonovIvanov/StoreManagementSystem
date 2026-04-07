package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Product;
import store.model.ProductCategory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceImplTest {

    private PricingService pricingService;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        // Параметри: хранителни +20%, нехранителни +30%, праг 5 дни, намаление 15%
        pricingService = new PricingServiceImpl(0.20, 0.30, 5, 0.15);
        today = LocalDate.now();
    }

    @Test
    void testFoodProductNormalPrice() {
        Product food = new Product(1, "Хляб", 1.00,
                today.plusDays(10), ProductCategory.FOOD);
        double price = pricingService.calculateSellingPrice(food);
        // 1.00 * (1 + 0.20) = 1.20
        assertEquals(1.20, price, 0.001);
    }

    @Test
    void testNonFoodProductNormalPrice() {
        Product nonFood = new Product(2, "Сапун", 1.00,
                today.plusDays(10), ProductCategory.NON_FOOD);
        double price = pricingService.calculateSellingPrice(nonFood);
        // 1.00 * (1 + 0.30) = 1.30
        assertEquals(1.30, price, 0.001);
    }

    
}