package entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

/**
 * In-memory order book for a single stock.
 * - buyOrders:  descending by price (highest bid first = best for seller)
 * - sellOrders: ascending by price  (lowest ask first  = best for buyer)
 * Same price → FIFO queue (price-time priority).
 */
public class OrderBook {

    private final String stockId;
    private final TreeMap<Double, Queue<Order>> buyOrders;
    private final TreeMap<Double, Queue<Order>> sellOrders;

    public OrderBook(String stockId) {
        this.stockId = stockId;
        this.buyOrders = new TreeMap<>(Collections.reverseOrder());
        this.sellOrders = new TreeMap<>();
    }

    public String getStockId() {
        return stockId;
    }

    public TreeMap<Double, Queue<Order>> getBuyOrders() {
        return buyOrders;
    }

    public TreeMap<Double, Queue<Order>> getSellOrders() {
        return sellOrders;
    }

    public void addBuyOrder(Order order) {
        buyOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
    }

    public void addSellOrder(Order order) {
        sellOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
    }
}
