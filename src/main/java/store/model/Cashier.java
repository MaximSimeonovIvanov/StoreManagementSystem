package store.model;

public class Cashier {
    //constants
    private final int id;
    private final String name;
    private final double monthlySalary;

    //constructor
    public Cashier(int id, String name, double monthlySalary) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (monthlySalary < 0) throw new IllegalArgumentException("Salary cannot be negative");

        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    //getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getMonthlySalary() {
        return monthlySalary;
    }
}
