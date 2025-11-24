package store.model;

import store.service.*;
import store.util.ReceiptFileWriter;
import java.util.List;

public class Store {

    private final String name;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final ReceiptService receiptService;
    private final ProductService productService;
    private final CashierService cashierService;


    public Store(String name,
                 double markupFoodPercent,
                 double markupNonFoodPercent,
                 int expiryDaysThreshold,
                 double expiryDiscountPercent) {

        this.name = name;

        this.inventoryService = new InventoryServiceImpl();
        this.pricingService = new PricingServiceImpl(
                markupFoodPercent, markupNonFoodPercent,
                expiryDaysThreshold, expiryDiscountPercent
        );
        this.receiptService = new ReceiptServiceImpl();
        this.productService = new ProductServiceImpl();
        this.cashierService = new CashierServiceImpl();
    }

    public void addProduct(Product product, int initialQuantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("initial quantity cannot be negatie");
        }

        productService.addProduct(product);
        inventoryService.addStock(product.getId(), initialQuantity);
    }

    public int getProductQuantity(int productId) {
        return inventoryService.getStock(productId);
    }

    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    public void addCashier(Cashier cashier){
        cashierService.addCashier(cashier);
    }

    public List<Cashier> getCashiers(){
        return cashierService.getAllCashiers();
    }

    public Receipt createReceipt(Cashier cashier) {
        if (cashier == null){
            throw new IllegalArgumentException("cashier cannot be null");
        }

        Cashier existingCashier = cashierService.findCashierById(cashier.getId());
        if (existingCashier == null){
            throw new IllegalArgumentException("cashier with ID "+cashier.getId() + "not found in system");
        }

        return receiptService.createReceipt(existingCashier);
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
        Product product = productService.findProductById(productId);
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

        ReceiptFileWriter.saveReceiptToFile(receipt);
    }
}
