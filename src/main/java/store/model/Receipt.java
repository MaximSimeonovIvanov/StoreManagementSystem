package store.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receipt {
    private final String id;
    private final Cashier cashier;
    private final LocalDateTime dateTime;
    private final List<ReceiptItem> items;

    public Receipt(String id, Cashier cashier) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Receipt ID cannot be null or empty");
        }
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }

        this.id = id;
        this.cashier = cashier;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<ReceiptItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(ReceiptItem item) {
        if (item == null) {
            throw new IllegalArgumentException("ReceiptItem cannot be null");
        }
        items.add(item);
    }

    public double getTotal() {
        double sum = 0;
        for (ReceiptItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Receipt ").append(id).append(" ===\n");
        sb.append("Cashier: ").append(cashier.getName()).append("\n");
        sb.append("Date: ").append(dateTime).append("\n\n");

        for (ReceiptItem item : items) {
            sb.append(item.toString()).append("\n");
        }

        sb.append("---------------------------\n");
        sb.append(String.format("TOTAL: %.2f лв.\n", getTotal()));

        return sb.toString();
    }
}