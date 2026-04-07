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
        assertThrows(IllegalArgumentException.class, () -> cashierService.addCashier(null));
    }

    @Test
    void testAddCashierDuplicateIdThrows(){
        Cashier cashier1 = new Cashier(1, "Ivan", 1500.0);
        Cashier cashier2 = new Cashier(1, "Petran", 1600.0);
        cashierService.addCashier(cashier1);
        assertThrows(IllegalArgumentException.class, () -> cashierService.addCashier(cashier2));
    }

    @Test
    void testFindCashierByIdExists() {
        Cashier cashier = new Cashier(1, "Иван", 1500.0);
        cashierService.addCashier(cashier);
        Cashier found = cashierService.findCashierById(1);
        assertNotNull(found);
        assertEquals(1, found.getId());
        assertEquals("Иван", found.getName());
        assertEquals(1500.0, found.getMonthlySalary());
    }

    @Test
    void testFindCashierByIdNotExist(){
        Cashier found = cashierService.findCashierById(9999);
        assertNull(found);
    }

    @Test
    void testGetAllCashiers() {
        Cashier cashier1 = new Cashier(1, "Иван", 1500.0);
        Cashier cashier2 = new Cashier(2, "Георги", 1400.0);
        cashierService.addCashier(cashier1);
        cashierService.addCashier(cashier2);

        List<Cashier> all = cashierService.getAllCashiers();
        assertEquals(2, all.size());
        //списъкът е копиe
        assertThrows(UnsupportedOperationException.class,
                () -> all.add(new Cashier(3, "Тест", 1000.0)));
    }
}
