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
                 InventoryService inventoryService,
                 PricingService pricingService,
                 ReceiptService receiptService,
                 ProductService productService,
                 CashierService cashierService) {
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("dtore name cannot be empty");
        }

        this.name = name;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.receiptService = receiptService;
        this.productService = productService;
        this.cashierService = cashierService;
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


    public void addProductToReceipt(String receiptId, int productId, int quantity){
        Receipt receipt = findReceipt(receiptId);
        Product product = findProduct(productId);
        validateStock(product, quantity);
        ReceiptItem item = createReceiptItem(product, quantity);
        addItemToReceipt(receipt, item);
        updateInventory(product, quantity);
        saveReceiptToFile(receipt);
    }
    private Receipt findReceipt(String receiptId){
        Receipt receipt = receiptService.findReceiptById(receiptId);
        if (receipt == null) {
            throw new IllegalArgumentException("receipt with ID "+receiptId+" not found");
        }
        return receipt;
    }
    private Product findProduct(int productId){
        Product product = productService.findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("product with ID "+productId+" not found");
        }
        return product;
    }
    private void validateStock(Product product, int quantity){
        inventoryService.checkAvailability(product.getId(), quantity);
    }
    private ReceiptItem createReceiptItem(Product product, int quantity){
        double sellingPrice = pricingService.calculateSellingPrice(product);
        return new ReceiptItem(product, quantity, sellingPrice);
    }
    private void addItemToReceipt(Receipt receipt, ReceiptItem item){
        receipt.addItem(item);
    }
    private void updateInventory(Product product, int quantity){
        inventoryService.reduceStock(product.getId(), quantity);
    }
    private void saveReceiptToFile(Receipt receipt){
        ReceiptFileWriter.saveReceiptToFile(receipt);
    }
}
