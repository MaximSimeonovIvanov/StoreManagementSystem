package store;

import store.model.*;
import store.exception.InsufficientStockException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            Store store = new Store("Моят магазин", 0.20, 0.30, 5, 0.15);

            Cashier cashier = new Cashier(1, "Максим Иванов", 1500.0);
            store.addCashier(cashier);

            Product milk = new Product(101, "Мляко", 2.00,
                    LocalDate.now().plusDays(3), ProductCategory.FOOD);
            Product chocolate = new Product(102, "Шоколад", 1.50,
                    LocalDate.now().plusDays(30), ProductCategory.FOOD);

            store.addProduct(milk, 25);
            store.addProduct(chocolate, 10);

            Receipt receipt = store.createReceipt(cashier);
            System.out.println("Създадена касова бележка: " + receipt.getId());

            store.addProductToReceipt(receipt.getId(), 101, 2);
            store.addProductToReceipt(receipt.getId(), 102, 1);

            System.out.println("\n=== РЕЗУЛТАТИ ===");
            System.out.println("Обща сума: " + receipt.getTotal() + " лв.");
            System.out.println("Наличност мляко: " + store.getProductQuantity(101));
            System.out.println("Наличност шоколад: " + store.getProductQuantity(102));
            System.out.println("Брой касови бележки: " + store.getReceiptsCount());

        } catch (Exception e) {
            System.out.println("Грешка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}