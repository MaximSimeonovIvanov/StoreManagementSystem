package store.service;

import store.model.Receipt;
import store.model.ReceiptItem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class ReceiptFileServiceImpl implements ReceiptFileService{

    private final String receiptsDirectory;

    //по подразбиране
    public ReceiptFileServiceImpl() {
        this("receipts");
    }

    //за тестове
    public ReceiptFileServiceImpl(String receiptsDirectory) {
        this.receiptsDirectory = receiptsDirectory;
    }

    @Override
    public void saveReceipt(Receipt receipt){
        if (receipt.getItems().isEmpty()) {
            return; //taka ne sazdava file za prazni belejki
        }

        //използвам полето receiptDirectory, а не фиксиран низ
        new File(receiptsDirectory).mkdirs();

        String fileName = receiptsDirectory+File.separator+"receipt_"+receipt.getId()+".txt";

        try(FileWriter writer = new FileWriter(fileName)){
            writer.write(generateReceiptContent(receipt));
            System.out.println("бележка записана във файл: "+fileName);
        } catch (IOException e){
            System.out.println("грещка при запис на бележка: "+e.getMessage());
        }
    }

    private String generateReceiptContent(Receipt receipt){
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
