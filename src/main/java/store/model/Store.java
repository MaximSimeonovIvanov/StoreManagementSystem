package store.model;
//Store е единствената точка за достъп до цялата бизнес логика
import store.service.*;
import java.util.List;

public class Store {
    private final String name;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final ReceiptService receiptService;
    private final ProductService productService;
    private final CashierService cashierService;
    private final ReceiptFileService receiptFileService;
    private final RegisterService registerService;

    public Store(String name,
                 InventoryService inventoryService,
                 PricingService pricingService,
                 ReceiptService receiptService,
                 ProductService productService,
                 CashierService cashierService,
                 ReceiptFileService receiptFileService,
                 RegisterService registerService) {
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("dtore name cannot be empty");
        }
        //инциализациите
        this.name = name;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.receiptService = receiptService;
        this.productService = productService;
        this.cashierService = cashierService;
        this.receiptFileService = receiptFileService;
        this.registerService = registerService;
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
        productService.recordProductDelivery(product.getId(), initialQuantity);//записва доставката
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
        //saveReceiptToFile(receipt); това вече се прави в finalizePurchase
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
        productService.recordProductSale(product.getId(), quantity);//zapisva prodajbata chrez service-a
    }
    private void saveReceiptToFile(Receipt receipt){
        receiptFileService.saveReceipt(receipt); //така ползвам новия интерфехс
        //ReceiptFileWriter.saveReceiptToFile(receipt); //това е статичен coupling и не е добра практика“
    }

    public void finalizePurchase(String receiptId, Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        Receipt receipt = findReceipt(receiptId);
        double total = receipt.getTotal();

        //дали клиентът има пари
        if (!customer.canAfford(total)) {
            throw new IllegalStateException(
                    String.format("Клиент: %s няма достатъчно пари. Нужни: %.2f лв., Има: %.2f лв.",
                            customer.getName(), total, customer.getWalletBalance())
            );
        }

        //плащане
        customer.deductMoney(total);

        //Записване на файл (вече знаем че покупката е успешна)
        saveReceiptToFile(receipt);

        System.out.printf("Успешна покупка! Оставащи пари: %.2f лв.%n", customer.getWalletBalance());
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Receipt receipt : receiptService.getAllReceipts()) {
            total += receipt.getTotal();
        }
        return total;
    }

    public double getTotalSupplyCosts() {
        double total = 0;
        for (Product product : productService.getAllProducts()) {
            // Доставна цена на ПРОДАДЕНИТЕ продукти
            total += product.getSupplyPrice() * product.getQuantitySold();
        }
        return total;
    }

    //статистика
    public void printFinancialReport() {
        System.out.println("\n=== ФИНАНСОВ ОТЧЕТ ===");
        System.out.printf("Общ оборот: %.2f лв.\n", getTotalRevenue());
        System.out.printf("Разходи за доставки: %.2f лв.\n", getTotalSupplyCosts());
        System.out.println("======================");
    }

    //kasi
    public void addRegister(Register register){
        registerService.addRegister(register);
    }

    public void assignCashierToRegister(int cashierId, int registerId){
        Cashier cashier = cashierService.findCashierById(cashierId);
        Register register = registerService.findRegisterById(registerId);

        if (cashier == null) {
            throw new IllegalArgumentException("cashier not found");
        }
        if (register == null) {
            throw new IllegalArgumentException("register not found");
        }

        //дали друг касиер вече работи на тази каса
        for (Cashier c : cashierService.getAllCashiers()) {
            if (c.getCurrentRegister() != null && c.getCurrentRegister().getId() == registerId) {
                throw new IllegalStateException("Каса " + register.getName() + " вече е заета от касиер " + c.getName());
            }
        }

        cashier.assignToRegister(register);
        System.out.println("касиер "+cashier.getName()+" назначен на каса "+register.getName());
    }

    public List<Register>getRegisters(){
        return registerService.getAllRegisters();
    }
}
