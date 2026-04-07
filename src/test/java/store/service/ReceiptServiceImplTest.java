package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Cashier;
import store.model.Receipt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceImplTest {

    private ReceiptService receiptService;
    private Cashier cashier;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptServiceImpl();
        cashier = new Cashier(1, "Тест Касиер", 1500.0);
    }

    @Test
    void testCreateReceipt() {
        Receipt receipt = receiptService.createReceipt(cashier);
        assertNotNull(receipt);
        assertNotNull(receipt.getId());
        assertEquals(cashier, receipt.getCashier());
        assertNotNull(receipt.getDateTime());
        assertEquals(0, receipt.getItems().size());
        assertEquals(1, receiptService.getReceiptsCount());
    }

    @Test
    void testCreateReceiptWithNullCashierThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> receiptService.createReceipt(null));
    }

    @Test
    void testReceiptIdsAreUnique() {
        Receipt receipt1 = receiptService.createReceipt(cashier);
        Receipt receipt2 = receiptService.createReceipt(cashier);
        assertNotEquals(receipt1.getId(), receipt2.getId());
    }

    @Test
    void testFindReceiptByIdExists() {
        Receipt created = receiptService.createReceipt(cashier);
        Receipt found = receiptService.findReceiptById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getCashier(), found.getCashier());
    }

    @Test
    void testFindReceiptByIdNotExists() {
        Receipt found = receiptService.findReceiptById("R999");
        assertNull(found);
    }

    @Test
    void testGetReceiptsCount() {
        assertEquals(0, receiptService.getReceiptsCount());
        receiptService.createReceipt(cashier);
        assertEquals(1, receiptService.getReceiptsCount());
        receiptService.createReceipt(cashier);
        assertEquals(2, receiptService.getReceiptsCount());
    }
}