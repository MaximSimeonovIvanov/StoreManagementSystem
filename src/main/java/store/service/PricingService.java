package store.service;

import store.model.Product;

public interface PricingService {
    double calculateSellingPrice(Product product);
    boolean isProductExpired(Product product);
    boolean isNearExpiration(Product product);
}

//за разлика то inventory service, този сървис ще изисква данни за целия продукт - категория, доставна цена, срок на годност, име