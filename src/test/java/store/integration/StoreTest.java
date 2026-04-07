package store.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.*;
import store.service.*;
import store.exception.InsufficientStockException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {
    private Store store;
    private Cashier cashier;
    private Customer customer;

    @BeforeEach
    void setUp(){
        //sazdavam magazin s vsi4ki realni implementacii
        store = new Store(
                "ТЕСТОВ МАГАЗИН",
                new InventoryServiceImpl(),
                new PricingServiceImpl(0.20, 0.30, 5, 0.15),
                new ReceiptServiceImpl(),
                new ProductServiceImpl(),
                new CashierServiceImpl(),
                new ReceiptFileServiceImpl(),
                new RegisterServiceImpl()
        );

        cashier = new Cashier(1, "Тест Касиер", 1500.0);
        store.addCashier(cashier);
    }

    @Test
    void testAddProductAndCheckStock() {
        Product milk = new Product(101, "Мляко", 2.00, LocalDate.now().plusDays(10), ProductCategory.FOOD);
        store.addProduct(milk, 20);

        assertEquals(20, store.getProductQuantity(101));
        List<Product> products = store.getProducts();
        assertEquals(1, products.size());
        assertEquals("Мляко", products.get(0).getName());
    }

    @Test
    void testCreateReceiptAndAddItems() {
        Product chocolate = new Product(102, "Шоколад", 1.50, LocalDate.now().plusDays(30), ProductCategory.FOOD);
        store.addProduct(chocolate, 10);

        Receipt receipt = store.createReceipt(cashier);
        assertNotNull(receipt);

        store.addProductToReceipt(receipt.getId(), 102, 2);

        assertEquals(1, receipt.getItems().size());
        assertEquals(3.60, receipt.getTotal(), 0.001);

        // nalichnostta trqbva da e namalqla
        assertEquals(8, store.getProductQuantity(102));
    }

    @Test
    void testFinalizePurchaseSuccess() {
        Product milk = new Product(101, "Мляко", 2.00, LocalDate.now().plusDays(3), ProductCategory.FOOD);
        store.addProduct(milk, 5);

        Receipt receipt = store.createReceipt(cashier);
        store.addProductToReceipt(receipt.getId(), 101, 2);

        Customer richCustomer = new Customer("Богат", 10.0);
        store.finalizePurchase(receipt.getId(), richCustomer);

        assertEquals(10.0 - receipt.getTotal(), richCustomer.getWalletBalance(), 0.001);
        assertEquals(3, store.getProductQuantity(101));
        assertEquals(1, store.getReceiptsCount());
        assertEquals(receipt.getTotal(), store.getTotalRevenue(), 0.001);
    }
}
