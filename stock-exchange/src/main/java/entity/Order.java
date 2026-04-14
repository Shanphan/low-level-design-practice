package entity;

import service.IdGenerator;

public class Order {

    private final String id;
    private final String userId;
    private final String stockId;
    private final OrderType type;
    private final double price;
    private final int quantity;
    private int remainingQuantity;
    private OrderStatus status;
    private final long timestamp;

    public Order(String userId, String stockId, OrderType type, double price, int quantity) {
        this.id = IdGenerator.createId("ORDER");
        this.userId = userId;
        this.stockId = stockId;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.OPEN;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getStockId() {
        return stockId;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + " [" + type + " " + quantity + " " + stockId + " @ " + price
                + ", remaining=" + remainingQuantity + ", status=" + status + "]";
    }
}
