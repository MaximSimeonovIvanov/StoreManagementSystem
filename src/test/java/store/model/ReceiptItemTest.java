package store.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class ReceiptItemTest {

    private Product testProduct;

    @BeforeEach
    void setUp(){
        testProduct = new Product(1, "Testov produkt", 10.0, LocalDate.now().plusDays(10), ProductCategory.FOOD);
    }

    @Test
    void testReceiptItemCreationValid() {
        ReceiptItem item = new ReceiptItem(testProduct, 3, 15.0);
        assertEquals(1, item.getProductId());
        assertEquals("Testov produkt", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(15.0, item.getSellingPrice());
        assertEquals(45.0, item.getSubtotal());
    }

    @Test
    void testReceiptItemNullProductThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReceiptItem(null, 1, 10.0));
    }

    @Test
    void testReceiptItemZeroQuantityThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReceiptItem(testProduct, 0, 10.0));
    }

    @Test
    void testReceiptItemNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReceiptItem(testProduct, -1, 10.0));
    }

    @Test
    void testReceiptItemNegativeSellingPriceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReceiptItem(testProduct, 1, -5.0));
    }

    @Test
    void testSubtotalCalculation() {
        ReceiptItem item = new ReceiptItem(testProduct, 5, 2.5);
        assertEquals(12.5, item.getSubtotal());
    }

    @Test
    void testToStringContainsProductNameAndQuantity() {
        ReceiptItem item = new ReceiptItem(testProduct, 2, 3.0);
        String str = item.toString();
        assertTrue(str.contains("Testov produkt"));
        assertTrue(str.contains("2 бр."));
        assertTrue(str.contains("3.00"));
        assertTrue(str.contains("6.00"));
    }
}
