package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Cashier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CashierServiceImplTest {
    private CashierService cashierService;

    @BeforeEach
    void setUp(){
        cashierService = new CashierServiceImpl();
    }

    @Test
    void testAddCashier(){
        Cashier cashier = new Cashier(1, "Ижан Петров", 1500.0);
        cashierService.addCashier(cashier);
        assertEquals(1, cashierService.getAllCashiers().size());
    }

    @Test
    void testAddCashierNullThrows(){
        assertThrows()
    }
}
