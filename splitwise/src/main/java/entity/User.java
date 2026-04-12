package entity;

import Service.IdGenerator;

public class User {

    String id;
    String name;

    public User(String name) {
        this.id = IdGenerator.createId("USER");
        this.name = name;
    }

    public String getId() {
        return id;
    }
}
