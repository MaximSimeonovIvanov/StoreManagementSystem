package store.service;

import store.model.Receipt;
import store.model.Cashier;

public interface ReceiptService {
    Receipt createReceipt(Cashier cashier);
    Receipt findReceiptById(String receiptId);
    int getReceiptsCount();
}