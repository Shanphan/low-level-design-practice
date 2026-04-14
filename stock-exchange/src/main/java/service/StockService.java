package service;

import entity.Stock;
import manager.StockMgr;

public class StockService {

    private final StockMgr stockMgr;

    public StockService(StockMgr stockMgr) {
        this.stockMgr = stockMgr;
    }

    public Stock addStock(Stock stock) {
        return stockMgr.save(stock);
    }
}
