package store.service;

import store.model.Product;
import java.util.List;
import java.util.ArrayList;

public class ProductServiceImpl implements ProductService{
    private final List<Product> products;

    public ProductServiceImpl(){
        this.products = new ArrayList<>();
    }

    @Override
    public void addProduct(Product product){
        if(product == null){
            throw new IllegalArgumentException("Produc cannot be null");
        }

        //проверка дублирашо ID
        for (Product p : products){
            if (p.getId()==product.getId()) {
                throw new IllegalArgumentException("Product with ID "+product.getId()+" already exists");
            }
        }

        products.add(product);
    }

    @Override
    public Product findProductById(int productId){
        for (Product product : products){
            if (product.getId() == productId){
                return product;
            }
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts(){
        return new ArrayList<>(products); //връща копие
    }

    @Override
    public void recordProductSale(int productId, int quantity) {
        Product product = findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        product.addSale(quantity);
    }

    @Override
    public void recordProductDelivery(int productId, int quantity) {
        Product product = findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        product.addDelivery(quantity);
    }


}
