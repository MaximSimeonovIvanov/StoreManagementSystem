package store.service;

import store.model.Cashier;
import java.util.List;

public interface CashierService {
    void addCashier(Cashier cashier);
    Cashier findCashierById(int cashierId);
    List<Cashier> getAllCashiers();
}
