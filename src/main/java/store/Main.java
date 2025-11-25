package store;

import store.model.*;
import store.exception.InsufficientStockException;
import java.time.LocalDate;
import store.service.*;

public class Main {
    public static void main(String[] args) {
        //декл. променливите тук
        Store store = null;
        Cashier cashier = null;

        try {
            //и инициализирам променливите тук
            store = new Store(
                    "Моят магазин",
                    new InventoryServiceImpl(),
                    new PricingServiceImpl(0.20, 0.30, 5, 0.15),
                    new ReceiptServiceImpl(),
                    new ProductServiceImpl(),
                    new CashierServiceImpl(),
                    new ReceiptFileServiceImpl()
            );
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
            System.out.println("\nНеуспешно създаване на store или cashier - тестовете се пропускат");
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

        System.out.println("\n  ТЕСТ 3: КЛИЕНТ С ПАРИ  ");
        try {
            Customer customer = new Customer("Иван Петров", 10.0); // Клиент с 10 лв.
            Receipt receipt4 = store.createReceipt(cashier);
            store.addProductToReceipt(receipt4.getId(), 101, 2); // Мляко 2 бр. x ~2.40 лв. = ~4.80 лв.
            store.finalizePurchase(receipt4.getId(), customer);
            System.out.println("УСПЕХ: Клиентът плати успешно!");
        } catch (Exception e) {
            System.out.println("ГРЕШКА: " + e.getMessage());
        }

        System.out.println("\n  ТЕСТ 4: КЛИЕНТ БЕЗ ПАРИ  ");
        try {
            Customer poorCustomer = new Customer("бедният", 1.0); // Клиент само с 1 лв.
            Receipt receipt5 = store.createReceipt(cashier);
            store.addProductToReceipt(receipt5.getId(), 101, 2); // Мляко 2 бр. = ~4.80 лв.
            store.finalizePurchase(receipt5.getId(), poorCustomer);
            System.out.println("ГРЕШКА: Това не трябва да се случи!");
        } catch (IllegalStateException e) {
            System.out.println("УСПЕХ: " + e.getMessage());
        }

        System.out.println("\n ФИНАНСОВ ОТЧЕТ ");
        System.out.printf("Общ оборот: %.2f лв.\n", store.getTotalRevenue());
        System.out.printf("Разходи заплати: %.2f лв.\n", store.getTotalSalaryCosts());
        System.out.printf("Разходи доставки: %.2f лв.\n", store.getTotalSupplyCosts());
        System.out.printf("Печалба: %.2f лв.\n", store.getProfit());
    }
}