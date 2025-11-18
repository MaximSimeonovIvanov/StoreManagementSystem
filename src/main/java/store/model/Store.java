package store.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
    private LocalDate lastReceiptDate = LocalDate.now(); //кога деняъ се смен

    private final Map<Integer, Integer> productQuantities;


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
        this.productQuantities = new HashMap<>();
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
        productQuantities.put(product.getId(), initialQuantity);
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

    public void addProductToReceipt(String receiptId, int productId, int quantity) {
        // 1. Намери бележката
        Receipt receipt = findReceiptById(receiptId);
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt with ID " + receiptId + " not found");
        }

        // 2. Намери продукта
        Product product = findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }

        // 3. Провери наличност (ще имплементираме по-късно)
        // 4. Изчисли продажната цена
        double sellingPrice = calculateSellingPrice(product);

        // 5. Създай ReceiptItem
        ReceiptItem item = new ReceiptItem(product, quantity, sellingPrice);

        // 6. Добави в бележката
        receipt.addItem(item);
    }

    private Receipt findReceiptById(String receiptId) {
        for (Receipt receipt : receipts) {
            if (receipt.getId().equals(receiptId)) {
                return receipt;
            }
        }
        return null;
    }

    private Product findProductById(int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    private double calculateSellingPrice(Product product) {
        // 1. Проверка за изтекъл срок
        if (isProductExpired(product)) {
            throw new IllegalStateException("Cannot sell expired product: " + product.getName());
        }

        // 2. Определи базовия markup според категорията
        double baseMarkup = getBaseMarkupForCategory(product.getCategory());
        double basePrice = product.getSupplyPrice() * (1 + baseMarkup);

        // 3. Провери за намаление при наближаващ срок
        if (isNearExpiration(product)) {
            double discount = expiryDiscountPercent;
            return basePrice * (1 - discount);
        }
        return basePrice;
    }

    private boolean isProductExpired(Product product) {
        return product.getExpiryDate().isBefore(LocalDate.now());
    }

    private boolean isNearExpiration(Product product) {
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = product.getExpiryDate();

        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
        return daysUntilExpiry <= expiryDaysThreshold && daysUntilExpiry >= 0;
    }

    private double getBaseMarkupForCategory(ProductCategory category) {
        return switch (category) {
            case FOOD -> markupFoodPercent;
            case NON_FOOD -> markupNonFoodPercent;
        };
    }
}
