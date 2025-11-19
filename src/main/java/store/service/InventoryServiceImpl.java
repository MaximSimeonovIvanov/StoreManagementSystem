package store.service;

import store.model.Product;
import store.exception.InsufficientStockException;
import java.util.HashMap;
import java.util.Map;

public class InventoryServiceImpl implements InventoryService {
    private final Map<Integer, Integer> productQuantities;

    public InventoryServiceImpl() {
        this.productQuantities = new HashMap<>();
    }

    @Override
    public void addStock(Product product, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Количествоъо не може да е отрицателно");
        productQuantities.put(product.getId(), quantity);
    }

    @Override
    public void reduceStock(int productId, int quantity) throws InsufficientStockException {
        checkAvailabilityById(productId, quantity);
        int current = productQuantities.get(productId);
        productQuantities.put(productId, current - quantity);
    }

    @Override
    public int getStock(int productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    @Override
    public void checkAvailability(Product product, int quantity) throws InsufficientStockException {
        checkAvailabilityById(product.getId(), quantity);
    }

    private void checkAvailabilityById(int productId, int quantity) throws InsufficientStockException {
        int available = getStock(productId);
        if (available < quantity) {
            throw new InsufficientStockException(productId, "Продукт", quantity, available);
        }
    }
}