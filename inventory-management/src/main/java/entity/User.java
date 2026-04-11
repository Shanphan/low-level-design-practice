package entity;

public class User {

    private String id;
    private String name;

    public User( String name) {
        this.id = IdGenerator.generate("USER");
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
