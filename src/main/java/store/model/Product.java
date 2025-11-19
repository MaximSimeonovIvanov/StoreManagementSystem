package store.model;

import java.time.LocalDate;

public class Product {
    private final int id;
    private final String name;
    private final LocalDate expiryDate;
    private final ProductCategory category;

    private final double supplyPrice;

    public Product(int id, String name, double supplyPrice, LocalDate expiryDate, ProductCategory category) {
        // валидации
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty");
        if (supplyPrice < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (expiryDate == null) throw new IllegalArgumentException("Expiry date cannot be null");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");

        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.expiryDate = expiryDate;
        this.category = category;
    }

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
