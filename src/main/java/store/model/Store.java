package store.model;

import store.service.InventoryService;
import store.service.InventoryServiceImpl;
import store.service.PricingService;
import store.service.PricingServiceImpl;
import store.service.ReceiptService;
import store.service.ReceiptServiceImpl;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import store.exception.InsufficientStockException;

public class Store {

    private final String name;
    private final List<Product> products;
    private final List<Cashier> cashiers;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final ReceiptService receiptService;


    public Store(String name,
                 double markupFoodPercent,
                 double markupNonFoodPercent,
                 int expiryDaysThreshold,
                 double expiryDiscountPercent) {

        this.name = name;

        this.products = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.inventoryService = new InventoryServiceImpl();
        this.pricingService = new PricingServiceImpl(
                markupFoodPercent, markupNonFoodPercent,
                expiryDaysThreshold, expiryDiscountPercent
        );
        this.receiptService = new ReceiptServiceImpl();
    }

    public void addProduct(Product product, int initialQuantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("initial quantity cannot be negatie");
        }

        //проверка за дублиране на id
        for (Product p : products) {
            if (p.getId() == product.getId()) {
                throw new IllegalArgumentException("product with id " + product.getId() + "already exists");
            }
        }
        products.add(product);
        inventoryService.addStock(product.getId(), initialQuantity);
    }

    public int getProductQuantity(int productId) {
        return inventoryService.getStock(productId);
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
        return receiptService.createReceipt(cashier);
    }

    public List<Receipt> getReceipts() {
        return receiptService.getAllReceipts();
    }

    public int getReceiptsCount() {
        return receiptService.getReceiptsCount();
    }

    public void addProductToReceipt(String receiptId, int productId, int quantity) {
        //намери бележката
        Receipt receipt = receiptService.findReceiptById(receiptId);
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt with ID " + receiptId + " not found");
        }

        //намери продукта
        Product product = findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }

        //проверява наличност
        inventoryService.checkAvailability(product.getId(), quantity);

        //продажна цена
        double sellingPrice = pricingService.calculateSellingPrice(product);

        // създава recipt item
        ReceiptItem item = new ReceiptItem(product, quantity, sellingPrice);

        //добавя в бележката
        receipt.addItem(item);

        // намаля наличност
        inventoryService.reduceStock(product.getId(), quantity);
    }

    private Product findProductById(int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}
