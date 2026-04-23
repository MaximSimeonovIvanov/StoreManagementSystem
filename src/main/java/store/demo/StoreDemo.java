package store.demo;

import store.model.*;
import store.service.*;

import java.awt.*;
import java.time.LocalDate;

public class StoreDemo{
    //glaven metod
    public static void main(String[] args){
        System.out.println("ДЕМОНСТРАТИВЕН МАГАЗИН\n");

        //създавам магазин
        Store store = createStore();

        //каси и касиери
        demonstrateStaffAndRegisters(store);

        //продукти и наличности
        demonstrateProducts(store);

        //продажби с клиенти
        demonstrateSales(store);

        //финансови отчети
        demonstrateFinance(store);

        //грешки
        demonstrateErrors(store);

        System.out.println("\n край не демонстрация");
    }

    //метод създ на магазин
    private static Store createStore(){
        System.out.println("1.създаване на магазин 'СИМ'");

        //obekt Store s vsichki dependencies
        Store store = new Store(
                "СИМ",
                new InventoryServiceImpl(),
                new PricingServiceImpl(0.20, 0.30, 5, 0.15),
                new ReceiptServiceImpl(),
                new ProductServiceImpl(),
                new CashierServiceImpl(),
                new ReceiptFileServiceImpl(),
                new RegisterServiceImpl()
        );
        System.out.println("МАГАЗИН СЪЗДАДЕН УСЕПШНО");
        return store;
    }

    private static void demonstrateStaffAndRegisters(Store store){
        System.out.println("\n2 персонал и каси");

        //създ и добавям касиери
        Cashier cashier1 = new Cashier(1, "pesho peshov", 1200.0);
        Cashier cashier2 = new Cashier(2, "ivan ivanov", 1100.0);
        store.addCashier(cashier1);
        store.addCashier(cashier2);
        System.out.println("добавени касиери: "+cashier1.getName()+", "+cashier2.getName());

        Register register1 = new Register(1, "каса 1");
        Register register2 = new Register(2, "kasa 2");
        store.addRegister(register1);
        store.addRegister(register2);
        System.out.println("добавени каси: " + register1.getName()+", "+register2.getName());

        //naznachavane kasieri na kasi
        store.assignCashierToRegister(1,1);//pesho na kasa 1
        store.assignCashierToRegister(2,2);//ivan na 2
        System.out.println("Касиери назначени на каси");
    }

    private static void demonstrateProducts(Store store){
        System.out.println("\n3 добавяне на продукти");
        //produkti s razlichni srokove
        Product milk = new Product(101, "мляко", 1.80, LocalDate.now().plusDays(3), ProductCategory.FOOD);
        Product chocolate = new Product(102, "шоколад", 1.20, LocalDate.now().plusDays(30),ProductCategory.FOOD);
        Product soap = new Product(201, "сапун", 0.80, LocalDate.now().plusDays(100), ProductCategory.NON_FOOD);

        //добавям продукти в магазина с начлна наличност
        store.addProduct(milk, 50);
        store.addProduct(chocolate, 30);
        store.addProduct(soap, 20);
        System.out.println("добавени продукти: мляко, шоколад, сапун");
        //наличности
        System.out.println("\n НАЛИЧНОСТ:");
        System.out.println("мляко: "+store.getProductQuantity(101)+" бр.");
        System.out.println("шоколад: "+store.getProductQuantity(102)+ "бр.");
        System.out.println("сапун: "+store.getProductQuantity(201)+"бр.");
    }

    private static void demonstrateSales(Store store){
        System.out.println("\n ПРОДАЖБИ");

        //клиенти с различни бюджети
        Customer richCustomer = new Customer("матей петров", 50.0);
        Customer normalCustomer = new Customer("лука йоанов", 20.0);

        System.out.println("създадени клиенти: "+richCustomer.getName()+" пазарува");
        Cashier cashier = store.getCashiers().get(0);
        Receipt receipt1 = store.createReceipt(cashier);

        //dobavya produkti v belezhkata
        store.addProductToReceipt(receipt1.getId(),101,2);
        store.addProductToReceipt(receipt1.getId(), 102,3);
        store.addProductToReceipt(receipt1.getId(),201, 1);

        //finalizira pokupka
        store.finalizePurchase(receipt1.getId(), richCustomer);

        //2ra prodazhba ot obiknoven chovek
        System.out.println("\n ПРОДАЖБА 2: "+normalCustomer.getName()+" pazaruva");
        Receipt receipt2 = store.createReceipt(cashier);
        store.addProductToReceipt(receipt2.getId(), 101, 1);//milk
        store.addProductToReceipt(receipt2.getId(), 102, 2);//choco
        store.finalizePurchase(receipt2.getId(), normalCustomer);

        System.out.println("Uspeshno zavarsheni prodajbi");
    }

    //metod za finansovite otcheti
    private static void demonstrateFinance(Store store){
        System.out.println("\n5 ФИНАНСОВ ОТЧЕТ СЛЕД ПРОДАЖБИ");
        //готов метод за финансов отчет
        store.printFinancialReport();

        //dopalnitelna statistika
        System.out.println("\n допълнителна статистика:");
        System.out.println("    общ брой бележки: "+store.getReceiptsCount());
        System.out.println("    общ брой касиери: "+store.getCashiers().size());
        System.out.println("    брой каси: "+store.getRegisters().size());
        System.out.println("    брой продукти: "+store.getProducts().size());
    }

    //demo na greshki
    private static void demonstrateErrors(Store store){
        System.out.println("\n6 ОБРАБОТКА НА ГРЕШКИ");
        //клиент без достатъчно пари
        System.out.println("\n грешка 1: клиент без достатъчно пари");
        Customer poorCustomer = new Customer("пенчо бедния", 2.0);
        Receipt receipt = store.createReceipt(store.getCashiers().get(0));
        store.addProductToReceipt(receipt.getId(), 101, 5);//5 mleka sa mu tvarde mnogo

        try{
            store.finalizePurchase(receipt.getId(), poorCustomer);
            System.out.println("това не трябва да се принтира, а, дано");
        } catch (IllegalStateException e){System.out.println(" УСПЕХ: ХВАНАТА ГРЕШКА: "+e.getMessage());}

        //недосатъчна наличност
        System.out.println("\n грешка 2: недостатъчна наличност на продукт");
        try{
            Receipt receipt2 = store.createReceipt(store.getCashiers().get(0));
            store.addProductToReceipt(receipt2.getId(), 101,1000);
            System.out.println("грешка: това не трябва да се случва");
            } catch (Exception e){System.out.println(" УСПЕХ: системата хвана грешка г"+e.getMessage());}

        //изтекъл продукт
        System.out.println("\n ГРЕШКА 3: опит за продажба на изтекъл продукт");
        try{
            Product expiredYogurt = new Product(999, "Кисело мляко", 1.50, LocalDate.now().minusDays(1), ProductCategory.FOOD);
            store.addProduct(expiredYogurt, 5);
            Receipt receipt3 = store.createReceipt(store.getCashiers().get(0));
            store.addProductToReceipt(receipt3.getId(), 999, 1);
            System.out.println("Greshka - tova ne tryabva da se sluchva");
        } catch (IllegalStateException e){System.out.println(" УПСЕХ: Системата хвана грешкатаааааа: "+e.getMessage());}

        System.out.println("\n Всички грешки бяха обработени правилно!");
    }
}