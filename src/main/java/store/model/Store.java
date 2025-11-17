package store.model;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private final String name;
    private final List<Product> products;
    private final List<Cashier> cashiers;
    private final List<Receipt> receipts;

    private double markupFoodPercent;
    private double markupNonFoodPercent;

    private int expiryDaysThreshold;
    private double expiryDiscountPercent;

    private int receiptCounter = 1;


    public Store(String name, double markupFoodPercent, double markupNonFoodPercent, int expiryDaysThreshold, double expiryDiscountPercent){

        this.name = name;
        this.markupFoodPercent = markupFoodPercent;
        this.markupNonFoodPercent = markupNonFoodPercent;
        this.expiryDaysThreshold = expiryDaysThreshold;
        this.expiryDiscountPercent = expiryDiscountPercent;

        products = new ArrayList<>();
        cashiers = new ArrayList<>();
        receipts = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        for (Product p : products) {
            if (p.getId() == product.getId()) {
                throw new IllegalArgumentException("Product ID already exists");
            }
        }
        products.add(product);  // ✅ ДОБАВЯМЕ продукта!
    }

    /*public boolean addProduct(Product product) {
        if (product == null) {
            return false;
        }
        for (Product p : products) {
            if (p.getId() == product.getId()) {
                return false;
            }
        }
        products.add(product);
        return true;
    }*/

    public void addCashier(Cashier cashier) {
        //null проверка
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }
        //проверка за дублиране
        if (hasCashierWithId(cashier.getId())) {
            throw new IllegalArgumentException("Cashier with ID " + cashier.getId() + " already exists");
        }
        //добавяне на касиер
        cashiers.add(cashier);
    }

    //помощен метод за проверка на дублиране
    private boolean hasCashierWithId(int id) {
        return cashiers.stream().anyMatch(cashier -> cashier.getId() == id);
    }

    public Receipt createReceipt(Cashier cashier) {
        //генерираме string id
        String receiptId = String.format("R%03d", receiptCounter++);
        return new Receipt(receiptId, cashier);
    }


}
