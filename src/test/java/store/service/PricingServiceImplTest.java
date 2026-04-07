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

    @Test
    void testFoodProductNearExpiration() {
        Product food = new Product(3, "Мляко", 2.00,
                today.plusDays(3), ProductCategory.FOOD);
        double price = pricingService.calculateSellingPrice(food);
        assertEquals(2.04, price, 0.001);
    }

    @Test
    void testNonFoodProductNearExpiration() {
        Product nonFood = new Product(4, "Шампоан", 5.00,
                today.plusDays(2), ProductCategory.NON_FOOD);
        double price = pricingService.calculateSellingPrice(nonFood);
        assertEquals(5.525, price, 0.001);
    }

    @Test
    void testProductExactlyOnThresholdIsNearExpiration() {
        Product food = new Product(5, "Кисело мляко", 1.00,
                today.plusDays(5), ProductCategory.FOOD);
        double price = pricingService.calculateSellingPrice(food);
        assertEquals(1.02, price, 0.001); // 1.00 * 1.20 * 0.85 = 1.02
    }

    @Test
    void testExpiredProductThrowsException() {
        Product expired = new Product(6, "Изтекло сирене", 1.00,
                today.minusDays(1), ProductCategory.FOOD);
        assertThrows(IllegalStateException.class,
                () -> pricingService.calculateSellingPrice(expired));
    }

    @Test
    void testIsProductExpired() {
        Product expired = new Product(7, "Лошо", 1.00,
                today.minusDays(1), ProductCategory.FOOD);
        Product fresh = new Product(8, "Добро", 1.00,
                today.plusDays(1), ProductCategory.FOOD);
        assertTrue(pricingService.isProductExpired(expired));
        assertFalse(pricingService.isProductExpired(fresh));
    }

    @Test
    void testIsNearExpiration() {
        Product near = new Product(9, "Близо", 1.00,
                today.plusDays(3), ProductCategory.FOOD);
        Product notNear = new Product(10, "Далеч", 1.00,
                today.plusDays(10), ProductCategory.FOOD);
        assertTrue(pricingService.isNearExpiration(near));
        assertFalse(pricingService.isNearExpiration(notNear));
    }

    @Test
    void testProductWithZeroDaysLeftIsNearExpiration() {
        // днес изтича
        Product expiresToday = new Product(11, "Днешен", 1.00,
                today, ProductCategory.FOOD);
        assertTrue(pricingService.isNearExpiration(expiresToday));
        double price = pricingService.calculateSellingPrice(expiresToday);
        assertEquals(1.02, price, 0.001); // трябва да е намалена
    }
}