package store.service;

import store.model.Product;
import store.model.ProductCategory;
import java.time.LocalDate;

public class PricingServiceImpl implements PricingService {
    private final double markupFoodPercent;
    private final double markupNonFoodPercent;
    private final int expiryDaysThreshold;
    private final double expiryDiscountPercent;

    public PricingServiceImpl(double markupFoodPercent, double markupNonFoodPercent, int expiryDaysThreshold, double expiryDiscountPercent) {
        this.markupFoodPercent = markupFoodPercent;
        this.markupNonFoodPercent = markupNonFoodPercent;
        this.expiryDaysThreshold = expiryDaysThreshold;
        this.expiryDiscountPercent = expiryDiscountPercent;
    }

    @Override
    public double calculateSellingPrice(Product product) {
        if (isProductExpired(product)) {
            throw new IllegalStateException("Cannot sell expired product: " + product.getName());
        }

        double baseMarkup = getBaseMarkupForCategory(product.getCategory());
        double basePrice = product.getSupplyPrice() * (1 + baseMarkup);

        if (isNearExpiration(product)) {
            return basePrice * (1 - expiryDiscountPercent);
        }
        return basePrice;
    }

    @Override
    public boolean isProductExpired(Product product) {
        return product.getExpiryDate().isBefore(LocalDate.now());
    }

    @Override
    public boolean isNearExpiration(Product product) {
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), product.getExpiryDate());
        return daysUntilExpiry <= expiryDaysThreshold && daysUntilExpiry >= 0;
    }

    private double getBaseMarkupForCategory(ProductCategory category) {
        return switch (category) {
            case FOOD -> markupFoodPercent;
            case NON_FOOD -> markupNonFoodPercent;
        };
    }
}