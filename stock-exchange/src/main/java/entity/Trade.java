package entity;

import service.IdGenerator;

public class Trade {

    private final String id;
    private final String buyOrderId;
    private final String sellOrderId;
    private final String stockId;
    private final double price;
    private final int quantity;
    private final long timestamp;

    public Trade(String buyOrderId, String sellOrderId, String stockId, double price, int quantity) {
        this.id = IdGenerator.createId("TRADE");
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.stockId = stockId;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public String getStockId() {
        return stockId;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + " [" + quantity + " " + stockId + " @ " + price
                + ", buy=" + buyOrderId + ", sell=" + sellOrderId + "]";
    }
}
