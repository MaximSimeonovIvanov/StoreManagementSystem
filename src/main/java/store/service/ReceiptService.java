package store.service;

import store.model.Receipt;
import store.model.Cashier;

import java.util.List;

public interface ReceiptService {
    Receipt createReceipt(Cashier cashier);
    Receipt findReceiptById(String receiptId);
    int getReceiptsCount();
    List<Receipt> getAllReceipts();
}   