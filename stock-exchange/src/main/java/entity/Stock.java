package entity;

public class Stock {

    private final String id;      // e.g. "AAPL"
    private final String name;    // e.g. "Apple Inc"

    public Stock(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
