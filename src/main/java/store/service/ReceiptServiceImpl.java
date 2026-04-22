package store.service;

import store.model.Receipt;
import store.model.Cashier;
import java.util.ArrayList;
import java.util.List;

public class ReceiptServiceImpl implements ReceiptService {
    private final List<Receipt> receipts;
    private int receiptCounter = 1;

    public ReceiptServiceImpl() {
        this.receipts = new ArrayList<>();
    }

    @Override
    public Receipt createReceipt(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }

        String receiptId = "R" + receiptCounter;
        receiptCounter++;

        Receipt receipt = new Receipt(receiptId, cashier);
        receipts.add(receipt);
        //ReceiptFileWriter.saveReceiptToFile(receipt); ne zapisvam fail pri prazna belejka //статичен coupling
        return receipt;
    }

    @Override
    public Receipt findReceiptById(String receiptId) {
        for (Receipt receipt : receipts) {
            if (receipt.getId().equals(receiptId)) {
                return receipt;
            }
        }
        return null;
    }

    @Override
    public int getReceiptsCount() {
        return receipts.size();
    }

    @Override
    public List<Receipt> getAllReceipts(){
        return List.copyOf(receipts); //това е непроменим списък => запазвам Енкапсулацията !
        //return new ArrayList<>(receipts);//това е modifiable!!!
    }
}