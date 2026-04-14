package manager;

import entity.Trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeMgr {

    private final Map<String, Trade> trades = new HashMap<>();

    public Trade save(Trade trade) {
        trades.put(trade.getId(), trade);
        return trade;
    }

    public List<Trade> findAll() {
        return new ArrayList<>(trades.values());
    }
}
