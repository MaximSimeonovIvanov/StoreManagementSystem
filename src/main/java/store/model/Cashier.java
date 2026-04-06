package store.model;

public class Cashier {
    private final int id;
    private final String name;
    private final double monthlySalary;
    private Register currentRegister;

    public Cashier(int id, String name, double monthlySalary) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (monthlySalary < 0) throw new IllegalArgumentException("Salary cannot be negative");

        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
        this.currentRegister = null;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getMonthlySalary() {
        return monthlySalary;
    }
    public void assignToRegister(Register register){
        this.currentRegister = register;
    }
    public void unassignFromRegister(){
        this.currentRegister = null;
    }
    public Register getCurrentRegister(){
        return currentRegister;
    }
    public boolean isAssignedToRegister(){
        return currentRegister != null;
    }
}
