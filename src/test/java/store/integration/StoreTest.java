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

    
}
