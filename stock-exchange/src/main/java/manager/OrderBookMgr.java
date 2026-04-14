package manager;

import entity.OrderBook;

import java.util.HashMap;
import java.util.Map;

/**
 * One OrderBook per stock. Lazily created on first access.
 */
public class OrderBookMgr {

    private final Map<String, OrderBook> books = new HashMap<>();

    public OrderBook getOrCreate(String stockId) {
        return books.computeIfAbsent(stockId, OrderBook::new);
    }

    public OrderBook find(String stockId) {
        return books.get(stockId);
    }
}
