package store.model;

public class Customer {
    private final String name;
    private double walletBalance;

    public Customer(String name, double initialBalance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        this.name = name;
        this.walletBalance = initialBalance;
    }

    public String getName() {
        return name;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public boolean canAfford(double amount) {
        return walletBalance >= amount;
    }

    public void deductMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be negative or zero");
        }
        if (!canAfford(amount)) {
            throw new IllegalStateException(
                    String.format("Customer %s has insufficient funds. Required: %.2f, Available: %.2f",
                            name, amount, walletBalance)
            );
        }
        walletBalance -= amount;
    }

    @Override
    public String toString() {
        return String.format("Customer: %s, Balance: %.2f лв.", name, walletBalance);
    }
}