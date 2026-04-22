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

        cashier = new Cashier(1, "тест касиер", 1500.0);
        store.addCashier(cashier);
    }

    @Test
    void testAddProductAndCheckStock() {
        Product milk = new Product(101, "мляко", 2.00, LocalDate.now().plusDays(10), ProductCategory.FOOD);
        store.addProduct(milk, 20);

        assertEquals(20, store.getProductQuantity(101));
        List<Product> products = store.getProducts();
        assertEquals(1, products.size());
        assertEquals("мляко", products.get(0).getName());
    }

    @Test
    void testCreateReceiptAndAddItems() {
        Product chocolate = new Product(102, "шоколад", 1.50, LocalDate.now().plusDays(30), ProductCategory.FOOD);
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
        Product milk = new Product(101, "мляко", 2.00, LocalDate.now().plusDays(3), ProductCategory.FOOD);
        store.addProduct(milk, 5);

        Receipt receipt = store.createReceipt(cashier);
        store.addProductToReceipt(receipt.getId(), 101, 2);

        Customer richCustomer = new Customer("богат", 10.0);
        store.finalizePurchase(receipt.getId(), richCustomer);

        assertEquals(10.0 - receipt.getTotal(), richCustomer.getWalletBalance(), 0.001);
        assertEquals(3, store.getProductQuantity(101));
        assertEquals(1, store.getReceiptsCount());
        assertEquals(receipt.getTotal(), store.getTotalRevenue(), 0.001);
    }

    @Test
    void testFinalizePurchaseInsufficientFunds() {
        Product milk = new Product(101, "мляко", 2.00, LocalDate.now().plusDays(3), ProductCategory.FOOD);
        store.addProduct(milk, 5);

        Receipt receipt = store.createReceipt(cashier);
        store.addProductToReceipt(receipt.getId(), 101, 3);

        Customer poorCustomer = new Customer("беден", 2.0);
        assertThrows(IllegalStateException.class, () -> store.finalizePurchase(receipt.getId(), poorCustomer));

        //v tekushtata logika belejkata veche e v spisaka no bez zapisan fail
        assertEquals(1, store.getReceiptsCount());
    }

    @Test
    void testInsufficientStockThrowsException() {
        Product bread = new Product(103, "хляб", 1.00,
                LocalDate.now().plusDays(10), ProductCategory.FOOD);
        store.addProduct(bread, 2);

        Receipt receipt = store.createReceipt(cashier);
        assertThrows(InsufficientStockException.class,
                () -> store.addProductToReceipt(receipt.getId(), 103, 5));

        assertEquals(2, store.getProductQuantity(103));
        assertEquals(0, receipt.getItems().size());
    }

    @Test
    void testExpiredProductCannotBeSold() {
        Product expired = new Product(104, "изтекло", 1.00,
                LocalDate.now().minusDays(1), ProductCategory.FOOD);
        store.addProduct(expired, 5);

        Receipt receipt = store.createReceipt(cashier);
        assertThrows(IllegalStateException.class,
                () -> store.addProductToReceipt(receipt.getId(), 104, 1));
    }

    @Test
    void testAssignCashierToRegister() {
        Register register = new Register(1, "каса 1");
        store.addRegister(register);
        store.assignCashierToRegister(cashier.getId(), 1);

        //проверява касиерът е назначен на касата
        assertNotNull(cashier.getCurrentRegister());
        assertEquals(1, cashier.getCurrentRegister().getId());
    }
}
