package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.exception.InsufficientStockException;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceImplTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl();
    }

    @Test
    void testAddStock() {
        inventoryService.addStock(1, 10);
        assertEquals(10, inventoryService.getStock(1));

        inventoryService.addStock(1, 5);
        assertEquals(15, inventoryService.getStock(1));
    }

    @Test
    void testAddStockNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(1, -5));
    }

    @Test
    void testGetStockForNonExistentProductReturnsZero() {
        assertEquals(0, inventoryService.getStock(999));
    }

    @Test
    void testReduceStock() throws InsufficientStockException {
        inventoryService.addStock(1, 10);
        inventoryService.reduceStock(1, 3);
        assertEquals(7, inventoryService.getStock(1));
    }

    @Test
    void testReduceStockInsufficientThrows() {
        inventoryService.addStock(1, 5);
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.reduceStock(1, 10));
    }

    @Test
    void testCheckAvailabilitySuccess() throws InsufficientStockException {
        inventoryService.addStock(1, 10);
        // Не трябва да хвърли изключение
        inventoryService.checkAvailability(1, 5);
    }

    @Test
    void testCheckAvailabilityFailureThrows() {
        inventoryService.addStock(1, 5);
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.checkAvailability(1, 10));
    }

    @Test
    void testReduceStockWithExactAvailable() throws InsufficientStockException {
        inventoryService.addStock(1, 5);
        inventoryService.reduceStock(1, 5);
        assertEquals(0, inventoryService.getStock(1));
    }
}