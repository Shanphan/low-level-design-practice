package entity;

import service.IdGenerator;

import java.util.HashMap;
import java.util.Map;

public class User {

    private final String id;
    private final String name;
    private double balance;
    private final Map<String, Integer> holdings;  // stockId → shares owned

    public User(String name, double balance) {
        this.id = IdGenerator.createId("USER");
        this.name = name;
        this.balance = balance;
        this.holdings = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    @Override
    public String toString() {
        return name + " [balance=" + balance + ", holdings=" + holdings + "]";
    }
}
