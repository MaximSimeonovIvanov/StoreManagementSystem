package store.model;

public class ReceiptItem {
    private final int productId;
    private final String productName;
    private final double sellingPrice; //продажна цена
    private final int quantity;

    public ReceiptItem(Product product, int quantity, double sellingPrice) {
        //валидации
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative");
        }

        this.productId = product.getId();
        this.productName = product.getName();
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        //ако продуктът промени име/id/цена чрез евентуални setter-и, бележката остава същата
    }

    public int getProductId() {return productId;}
    public String getProductName() { return productName;}
    public int getQuantity() { return quantity; }
    public double getSellingPrice() { return sellingPrice; }

    // изчисление
    public double getSubtotal() {
        return quantity * sellingPrice;
        //отговорност на ReceiptItem, защото знае своето quantity и price
    }

    @Override
    public String toString() {
        return String.format("%s - %d бр. x %.2f лв. = %.2f лв.",
                productName, quantity, sellingPrice, getSubtotal());
        //предоставя четим изход при печат и debugging
    }

}