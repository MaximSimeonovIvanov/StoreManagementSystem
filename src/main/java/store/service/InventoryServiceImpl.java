package store.service;

import store.exception.InsufficientStockException;
import java.util.HashMap;
import java.util.Map;

public class InventoryServiceImpl implements InventoryService {

    private final Map<Integer, Integer> productQuantities;

    public InventoryServiceImpl() {
        this.productQuantities = new HashMap<>();
    }

    @Override
    public void addStock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative or zero");
        }

        int current = productQuantities.getOrDefault(productId, 0);
        productQuantities.put(productId, current + quantity);
    }

    @Override
    public void reduceStock(int productId, int quantity) throws InsufficientStockException {
        checkAvailability(productId, quantity);
        int current = productQuantities.get(productId);
        productQuantities.put(productId, current - quantity);
    }

    @Override
    public int getStock(int productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    @Override
    public void checkAvailability(int productId, int quantity) throws InsufficientStockException {
        int available = getStock(productId);
        if (available < quantity) {
            throw new InsufficientStockException(productId, "Product", quantity, available);
        }
    }
}
