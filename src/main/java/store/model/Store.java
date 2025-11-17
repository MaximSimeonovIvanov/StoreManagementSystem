package store.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    private int receiptCounter = 1;       //брояч за текущия ден
    private LocalDate lastReceiptDate = LocalDate.now(); //кога деняъ се сменя


    public Store(String name,
                 double markupFoodPercent,
                 double markupNonFoodPercent,
                 int expiryDaysThreshold,
                 double expiryDiscountPercent) {

        this.name = name;
        this.markupFoodPercent = markupFoodPercent;
        this.markupNonFoodPercent = markupNonFoodPercent;
        this.expiryDaysThreshold = expiryDaysThreshold;
        this.expiryDiscountPercent = expiryDiscountPercent;

        this.products = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.receipts = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.add(product);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public void addCashier(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }

        if (hasCashierWithId(cashier.getId())) {
            throw new IllegalArgumentException(
                    "Cashier with ID " + cashier.getId() + " already exists");
        }

        cashiers.add(cashier);
    }

    private boolean hasCashierWithId(int id) {
        for (Cashier cashier : cashiers) {
            if (cashier.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public List<Cashier> getCashiers() {
        return Collections.unmodifiableList(cashiers);
    }

    public Receipt createReceipt(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }

        String id = generateReceiptId(); //генерира ID

        Receipt receipt = new Receipt(id, cashier);

        receipts.add(receipt);

        return receipt;
    }

    public List<Receipt> getReceipts() {
        return Collections.unmodifiableList(receipts);
    }

    public int getReceiptsCount() {
        return receipts.size();
    }

    private String generateReceiptId() {
        return "R" + receiptCounter++;
    }
}
