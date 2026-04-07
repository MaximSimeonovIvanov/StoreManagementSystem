package store.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ReceiptItemTest {

    private Product testProduct;

    @BeforeEach
    void setUp(){
        testProduct = new Product(1, "Testov produkt", 10.0, LocalDate.now().plusDays(10), ProductCategory.FOOD);
    }

    @Test
    
}
