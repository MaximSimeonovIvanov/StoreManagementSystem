package store.model;

public class Register {
    private int id;
    private String name;

    public Register(int id, String name) {
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
