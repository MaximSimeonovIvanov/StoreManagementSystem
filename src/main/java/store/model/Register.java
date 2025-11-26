package store.model;

public class Register {
    private int id;
    private String name;

    public Register(int id, String name) {
        if (id<=0){
            throw new IllegalArgumentException("register ID cannot be negative");
        }
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("register cannot be null or empty");
        }
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
