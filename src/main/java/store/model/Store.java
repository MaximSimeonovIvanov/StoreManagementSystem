package store.model;

import store.service.InventoryService;
import store.service.InventoryServiceImpl;
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
    private final List<Receipt> receipts;

    private double markupFoodPercent;
    private double markupNonFoodPercent;
    private int expiryDaysThreshold;
    private double expiryDiscountPercent;

    private int receiptCounter = 1;       //брояч за текущия ден
//    private final Map<Integer, Integer> productQuantities;
    private final InventoryService inventoryService;


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
//        this.productQuantities = new HashMap<>();
        this.inventoryService = new InventoryServiceImpl();
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
//        productQuantities.put(product.getId(), initialQuantity);
        inventoryService.addStock(product, initialQuantity);
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
        //намери бележката
        Receipt receipt = findReceiptById(receiptId);
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
        double sellingPrice = calculateSellingPrice(product);

        // създава recipt item
        ReceiptItem item = new ReceiptItem(product, quantity, sellingPrice);

        //добавя в бележката
        receipt.addItem(item);

        // намаля наличност
        inventoryService.reduceStock(productId, quantity);
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
        //проверка за изтекъл срок
        if (isProductExpired(product)) {
            throw new IllegalStateException("Cannot sell expired product: " + product.getName());
        }

        // markup според категорията
        double baseMarkup = getBaseMarkupForCategory(product.getCategory());
        double basePrice = product.getSupplyPrice() * (1 + baseMarkup);

        //намаление при наближаващ срок
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

//    private void checkStockAvailability(Product product, int requestedQuantity) {
//        // текущото количество
//        Integer availableQuantity = productQuantities.get(product.getId());
//
//        // дали продуктът съществува
//        if (availableQuantity == null) {
//            throw new InsufficientStockException(product.getId(), product.getName(), requestedQuantity, 0);
//        }
//
//        //дали има достатъчно
//        if (availableQuantity < requestedQuantity) {
//            throw new InsufficientStockException(product.getId(), product.getName(), requestedQuantity, availableQuantity);
//        }
//    }

//    private void reduceProductQuantity(int productId, int quantity) {
//        int currentQuantity = productQuantities.get(productId);
//
//        int newQuantity = currentQuantity - quantity;
//
//        if (newQuantity < 0) {
//            throw new IllegalStateException(
//                    "Quantity cannot become negative for product ID: " + productId
//            );
//        }
//        productQuantities.put(productId, newQuantity);
//    }

//    public int getProductQuantity(int productId) {
//        return productQuantities.getOrDefault(productId, 0);
//        //без този метод ще ми хвърли null pointer exception
//    }

}
