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
}
