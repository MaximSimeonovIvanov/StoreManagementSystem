package store.service;

import store.model.Product;
import store.exception.InsufficientStockException;

public interface InventoryService {
    void addStock(Product product, int quantity);
    void reduceStock(int productId, int quantity) throws InsufficientStockException;
    int getStock(int productId);
    void checkAvailability(Product product, int quantity) throws InsufficientStockException;
}