package store.model;

import java.time.LocalDate;

public class Product {
    private final int id;
    private final String name;
    private final LocalDate expiryDate;
    private final ProductCategory category;
    private final double supplyPrice;

    //проследявам на доставки и продажби
    private int quantityDelivered;
    private int quantitySold;

    public Product(int id, String name, double supplyPrice, LocalDate expiryDate, ProductCategory category) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name cannot be null or empty");
        if (supplyPrice < 0) throw new IllegalArgumentException("price cannot be negative");
        if (expiryDate == null) throw new IllegalArgumentException("expiry date cannot be null");
        if (category == null) throw new IllegalArgumentException("category cannot be null");

        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.expiryDate = expiryDate;
        this.category = category;
        this.quantityDelivered = 0;
        this.quantitySold = 0;
    }

    public void addDelivery(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity cannot be negative или нула");
        this.quantityDelivered += quantity;
    }
    public void addSale(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity cannot be negative или нула");
        if (quantity > getAvailableQuantity()) {
            throw new IllegalStateException("not enough stock for sale");
        }
        this.quantitySold += quantity;
    }
    public int getQuantityDelivered() { return quantityDelivered; }
    public int getQuantitySold() { return quantitySold; }
    public int getAvailableQuantity() { return quantityDelivered - quantitySold; }


    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getSupplyPrice() {
        return supplyPrice;
    }
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    public ProductCategory getCategory() {
        return category;
    }


}
