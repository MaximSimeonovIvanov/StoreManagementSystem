package store.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import store.model.*;
import store.service.*;
import store.exception.InsufficientStockException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {
    @TempDir
    Path tempDir;

    private Store store;
    private Cashier cashier;

    @BeforeEach
    void setUp(){
        ReceiptFileService fileService = new ReceiptFileServiceImpl(tempDir.toString());
        //sazdavam magazin s vsi4ki realni implementacii
        store = new Store(
                "ТЕСТОВ МАГАЗИН",
                new InventoryServiceImpl(),
                new PricingServiceImpl(0.20, 0.30, 5, 0.15),
                new ReceiptServiceImpl(),
                new ProductServiceImpl(),
                new CashierServiceImpl(),
                fileService,
                new RegisterServiceImpl()
        );

        cashier = new Cashier(1, "тест касиер", 1500.0);
        store.addCashier(cashier);
    }

    @Test
    @DisplayName("добавяне на продукт и проверяване на наличност")
    void testAddProductAndCheckStock() {
        Product milk = new Product(101, "мляко", 2.00, LocalDate.now().plusDays(10), ProductCategory.FOOD);
        store.addProduct(milk, 20);

        assertEquals(20, store.getProductQuantity(101));
        List<Product> products = store.getProducts();
        assertEquals(1, products.size());
        assertEquals("мляко", products.get(0).getName());
    }

    @Test
    @DisplayName("създаване на бележка и добавяне на продукти в нея")
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
    @DisplayName("проверка за успешно създаване на файл")
    void testReceiptFileIsCreated() throws IOException{
        Product milk = new Product(101,"mlyako", 2.00,LocalDate.now().plusDays(10),ProductCategory.FOOD);
        store.addProduct(milk,5);
        Receipt receipt = store.createReceipt(cashier);
        store.addProductToReceipt(receipt.getId(), 101,2);
        Customer customer = new Customer("test klient", 10.0);
        store.finalizePurchase(receipt.getId(), customer);

        Path receiptFile = tempDir.resolve("receipt_" + receipt.getId() + ".txt");
        //Path receiptFile = Path.of("receipts/receipt_" + receipt.getId()+".txt");
        assertTrue(Files.exists(receiptFile));
        String content = Files.readString(receiptFile);
        assertTrue(content.contains("mlyako"));
        assertTrue(content.contains(receipt.getId()));
    }

    @Test
    @DisplayName("успешно завършване на покупка")
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
    @DisplayName("опит за финализиране на покупка при недостиг на средтсва")
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
    @DisplayName("опит за покупка на повече продукт от наличното")
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
    @DisplayName("опит за продажба на продукт с изтекъл срок")
    void testExpiredProductCannotBeSold() {
        Product expired = new Product(104, "изтекло", 1.00,
                LocalDate.now().minusDays(1), ProductCategory.FOOD);
        store.addProduct(expired, 5);

        Receipt receipt = store.createReceipt(cashier);
        assertThrows(IllegalStateException.class,
                () -> store.addProductToReceipt(receipt.getId(), 104, 1));
    }

    @Test
    @DisplayName("назначаване на касиер на каса")
    void testAssignCashierToRegister() {
        Register register = new Register(1, "каса 1");
        store.addRegister(register);
        store.assignCashierToRegister(cashier.getId(), 1);

        //проверява касиерът е назначен на касата
        assertNotNull(cashier.getCurrentRegister());
        assertEquals(1, cashier.getCurrentRegister().getId());
    }

    @Test
    @DisplayName("опит за назначаване на втори касиер на същата каса")
    void testAddingSecondCashierToARegisterThrowsException(){
        Cashier cashier2 = new Cashier(2, "втори касиер", 1400.0);
        store.addCashier(cashier2);

        Register register = new Register(1, "каса 1");
        store.addRegister(register);

        store.assignCashierToRegister(cashier.getId(), register.getId());

        assertThrows(IllegalStateException.class, () -> store.assignCashierToRegister(cashier2.getId(), register.getId()));

        assertEquals(register, cashier.getCurrentRegister());
        assertNull(cashier2.getCurrentRegister());
    }

    @Test
    @DisplayName("проверка на разходите за доставки на продадените стоки")
    void testTotalSupplyCosts() {
        Product milk = new Product(101, "мляко", 2.00, LocalDate.now().plusDays(10), ProductCategory.FOOD);
        store.addProduct(milk, 10);

        Receipt receipt = store.createReceipt(cashier);
        store.addProductToReceipt(receipt.getId(), 101, 3);
        Customer customer = new Customer("клиент", 20.0);
        store.finalizePurchase(receipt.getId(), customer);

        // очаквам = 3 * 2.00 = 6.00
        assertEquals(6.00, store.getTotalSupplyCosts(), 0.001);
    }
}
