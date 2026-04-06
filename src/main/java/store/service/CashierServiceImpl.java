package store.service;

import  store.model.Cashier;
import java.util.ArrayList;
import java.util.List;

public class CashierServiceImpl implements CashierService {
    private final List<Cashier> cashiers;

    public CashierServiceImpl(){
        this.cashiers = new ArrayList<>();
    }

    @Override
    public void addCashier(Cashier cashier){
        if(cashier == null){
            throw new IllegalArgumentException("cashier cannnot be null");
        }

        for(Cashier c : cashiers){
            if(c.getId() == cashier.getId()){
                throw new IllegalArgumentException("Cashier with ID "+cashier.getId() + " aready exists");
            }
        }
        cashiers.add(cashier);
    }

    @Override
    public Cashier findCashierById(int cashierId){
        for (Cashier cashier : cashiers){
            if (cashier.getId() == cashierId){
                return cashier;
            }
        }
        return null;
    }

    @Override
    public List<Cashier> getAllCashiers(){ return new ArrayList<>(cashiers);}
}
