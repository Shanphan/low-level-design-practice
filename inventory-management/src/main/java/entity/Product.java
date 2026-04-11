package entity;

import java.util.concurrent.locks.ReentrantLock;

public class Product {

    private String id;
    private String name;
    private Integer totalQuantity;
    private Integer reserveQuantity;
    private final ReentrantLock rowLock;

    public Product(String name, Integer totalQuantity) {
        this.rowLock = new ReentrantLock();
        this.id = IdGenerator.generate("PRODUCT");
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.reserveQuantity = 0;
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

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getReserveQuantity() {
        return reserveQuantity;
    }

    public void setReserveQuantity(Integer reserveQuantity) {
        this.reserveQuantity = reserveQuantity;
    }

    public ReentrantLock getRowLock() {
        return rowLock;
    }

    @Override
    public String toString() {
        return "Product [" +this.name + ", " + this.id + ", "+ this.totalQuantity + ", "+ this.reserveQuantity + "]";
    }
}
