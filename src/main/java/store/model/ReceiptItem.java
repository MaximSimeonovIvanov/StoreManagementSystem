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

    public String getProductName() {
        return product.getName();
        //директен достъп до името на продукта, когато трябва да покажем името в касовата бележка
    }

    // изчисление
    public double getSubtotal() {
        return quantity * sellingPrice;
        //отговорност на ReceiptItem, защото знае своето quantity и price
    }

    @Override
    public String toString() {
        return String.format("%s - %d бр. x %.2f лв. = %.2f лв.",
                product.getName(), quantity, sellingPrice, getSubtotal());
        // защо: предоставя четим изход при печат и debugging
    }

}