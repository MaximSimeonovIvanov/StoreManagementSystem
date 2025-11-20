package store.service;

import store.exception.InsufficientStockException;

public interface InventoryService {
    void addStock(int productId, int quantity);
    void reduceStock(int productId, int quantity) throws InsufficientStockException;
    int getStock(int productId);
    void checkAvailability(int productId, int quantity) throws InsufficientStockException;
}
