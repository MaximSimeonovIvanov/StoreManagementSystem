package store.service;

import store.model.Product;
import java.util.List;

public interface ProductService {
    void addProduct(Product product);
    Product findProductById(int productId);
    List<Product> getAllProducts();
}
