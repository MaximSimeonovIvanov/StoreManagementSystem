package store;

import store.model.*;
import store.exception.InsufficientStockException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        //декл. променливите тук
        Store store = null;
        Cashier cashier = null;

        try {
            //и инициализирам променливите тук
            store = new Store("Моят магазин", 0.20, 0.30, 5, 0.15);
            cashier = new Cashier(1, "Максим Иванов", 1500.0);
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

            System.out.println("\n  РЕЗУЛТАТИ   ");
            System.out.println("Обща сума: " + receipt.getTotal() + " лв.");
            System.out.println("Наличност мляко: " + store.getProductQuantity(101));
            System.out.println("Наличност шоколад: " + store.getProductQuantity(102));
            System.out.println("Брой касови бележки: " + store.getReceiptsCount());

        } catch (Exception e) {
            System.out.println("Грешка: " + e.getMessage());
            e.printStackTrace();
        }

        if (store != null && cashier != null) {
            System.out.println("\n  ТЕСТ 1: НЕДОСТАТЪЧНА НАЛИЧНОСТ  ");
            try {
                Receipt receipt2 = store.createReceipt(cashier);
                store.addProductToReceipt(receipt2.getId(), 101, 100);
                System.out.println("ГРЕШКА: Това не трябва да се случи!");
            } catch (InsufficientStockException e) {
                System.out.println("УСПЕХ: " + e.getMessage());
                System.out.println("Искани: " + e.getRequestedQuantity() + ", Налични: " + e.getAvailableQuantity());
            }
        } else {
            System.out.println("\n❌ Неуспешно създаване на store или cashier - тестовете се пропускат");
        }

        if (store != null && cashier != null) {
            System.out.println("\n  ТЕСТ 2: ИЗТЕКЪЛ ПРОДУКТ ");
            try {
                // изтекъл вчера
                Product expiredYogurt = new Product(103, "Кисело мляко", 1.20,
                        LocalDate.now().minusDays(1), ProductCategory.FOOD);

                store.addProduct(expiredYogurt, 5);

                Receipt receipt3 = store.createReceipt(cashier);
                store.addProductToReceipt(receipt3.getId(), 103, 1); //продажба на изтекъл продукт
                System.out.println("ГРЕШКА: Това не трябва да се случи!");
            } catch (IllegalStateException e) {
                System.out.println("УСПЕХ: " + e.getMessage());
            }
        }
    }


}