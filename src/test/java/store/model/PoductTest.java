package store.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PoductTest {

    private Product product;

    @BeforeEach
    void setUp(){
        //sazdava nov product predi vseki test
        product = new Product(1, "Мляко", 2.00,
                LocalDate.now().plusDays(10), ProductCategory.FOOD);
    }

    @Test
    void testProductCreationValid() {
        assertEquals(1, product.getId());
        assertEquals("Мляко", product.getName());
        assertEquals(2.00, product.getSupplyPrice());
        assertEquals(ProductCategory.FOOD, product.getCategory());
        assertEquals(0, product.getQuantityDelivered());
        assertEquals(0, product.getQuantitySold());
        assertEquals(0, product.getAvailableQuantity());
    }

    @Test
    void testProductCreationThrowsWhenIdIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(0, "Грешка", 1.0, LocalDate.now(), ProductCategory.FOOD));
        assertThrows(IllegalArgumentException.class,
                () -> new Product(-5, "Грешка", 1.0, LocalDate.now(), ProductCategory.FOOD));
    }

    @Test
    void testProductCreationThrowsWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(2, null, 1.0, LocalDate.now(), ProductCategory.FOOD));
    }

    @Test
    void testProductCreationThrowsWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(3, "   ", 1.0, LocalDate.now(), ProductCategory.FOOD));
    }

    @Test
    void testProductCreationThrowsWhenSupplyPriceNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(4, "Хляб", -0.5, LocalDate.now(), ProductCategory.FOOD));
    }

    @Test
    void testProductCreationThrowsWhenExpiryDateNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(5, "Сирене", 1.0, null, ProductCategory.FOOD));
    }

    @Test
    void testAddDelivery() {
        product.addDelivery(10);
        assertEquals(10, product.getQuantityDelivered());
        assertEquals(10, product.getAvailableQuantity());

        product.addDelivery(5);
        assertEquals(15, product.getQuantityDelivered());
        assertEquals(15, product.getAvailableQuantity());
    }

    @Test
    void testAddDeliveryNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> product.addDelivery(-1));
    }

    @Test
    void testAddSale() {
        product.addDelivery(10);
        product.addSale(3);
        assertEquals(3, product.getQuantitySold());
        assertEquals(7, product.getAvailableQuantity());
    }

    @Test
    void testAddSaleMoreThanAvailableThrows() {
        product.addDelivery(5);
        assertThrows(IllegalStateException.class,
                () -> product.addSale(10));
    }

    @Test
    void testAddSaleNegativeQuantityThrows() {
        product.addDelivery(5);
        assertThrows(IllegalArgumentException.class,
                () -> product.addSale(-2));
    }
}
