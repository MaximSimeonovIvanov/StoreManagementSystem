package store.model;

public class ReceiptItem {
    private final Product product;      //стока
    private final int quantity;         // Колко
    private final double sellingPrice;  // цена (различна от доставната)

    public ReceiptItem(Product product, int quantity, double sellingPrice) {
        // Валидации:
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative");
        }

        this.product = product;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getSellingPrice() { return sellingPrice; }

    // изчисление
    public double getSubtotal() {
        return quantity * sellingPrice;
        //отговорност на ReceiptItem, защото знае своето quantity и price
    }
}