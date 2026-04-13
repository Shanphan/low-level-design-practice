package entity;

import service.IdGenerator;

public class User {

    private String id;
    private String name;

    public User(String name) {
        this.id = IdGenerator.createId("USER");
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "USER [" +id + " name "+name+" ]";
    }
}
