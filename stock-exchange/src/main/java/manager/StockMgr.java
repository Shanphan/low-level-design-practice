package manager;

import entity.Stock;

import java.util.HashMap;
import java.util.Map;

public class StockMgr {

    private final Map<String, Stock> stocks = new HashMap<>();

    public Stock save(Stock stock) {
        stocks.put(stock.getId(), stock);
        return stock;
    }

    public Stock findById(String id) {
        return stocks.get(id);
    }

    public boolean existsById(String id) {
        return stocks.containsKey(id);
    }
}
