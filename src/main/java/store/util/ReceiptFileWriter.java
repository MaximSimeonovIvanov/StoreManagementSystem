package store.util;

import store.model.Receipt;
import store.model.ReceiptItem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class ReceiptFileWriter {
    public static void saveReceiptToFile(Receipt receipt){

        if (receipt.getItems().isEmpty()){
            return; //не създава файл за празни бележки
        }
        String folder = "receipts";
        new File(folder).mkdir();

        String fileName = folder + "/receipt_"+receipt.getId() + ".txt";

        try(FileWriter writer = new FileWriter(fileName)){
            writer.write(generateReceiptContent(receipt));
            System.out.println("бележка записана във файл: " + fileName);
        } catch (IOException e){
            System.out.println("грешка при запис на бележка: "+e.getMessage());
        }
    }

    private static String generateReceiptContent(Receipt receipt){
        StringBuilder content = new StringBuilder();

        content.append("   КАСОВА БЕЛЕЖКА   \n");
        content.append("Номер: ").append(receipt.getId()).append("\n");
        content.append("Касиер: ").append(receipt.getCashier().getName()).append("\n");
        content.append("Дата/час: ").append(receipt.getDateTime()).append("\n");
        content.append("----------------------------------------\n");

        content.append("ПРОДУКТИ:\n");
        for (ReceiptItem item : receipt.getItems()) {
            content.append(String.format("• %s - %d бр. x %.2f лв. = %.2f лв.\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getSellingPrice(),
                    item.getSubtotal()));
        }

        content.append("----------------------------------------\n");
        content.append(String.format("ОБЩА СУМА: %.2f лв.\n", receipt.getTotal()));
        content.append("========================================\n");
        content.append("Благодарим, че пазарувахте при нас!\n");

        return content.toString();
    }
}
