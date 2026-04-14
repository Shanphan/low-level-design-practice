package entity;

public class Gym {

    private String id;
    private String name;
    private String location;

    public Gym(String name, String location) {
        this.id = EntityIdGenerator.getId("GYM-");
        this.name = name;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
