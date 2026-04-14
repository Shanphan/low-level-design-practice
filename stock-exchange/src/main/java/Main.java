import entity.Stock;
import entity.User;
import manager.OrderBookMgr;
import manager.OrderMgr;
import manager.StockMgr;
import manager.TradeMgr;
import manager.UserMgr;
import service.OrderService;
import service.StockService;
import service.UserService;

public class Main {

    public static void main(String[] args) {
        // --- Wire dependencies ---
        UserMgr userMgr = new UserMgr();
        StockMgr stockMgr = new StockMgr();
        OrderMgr orderMgr = new OrderMgr();
        TradeMgr tradeMgr = new TradeMgr();
        OrderBookMgr orderBookMgr = new OrderBookMgr();

        UserService userService = new UserService(userMgr);
        StockService stockService = new StockService(stockMgr);
        OrderService orderService = new OrderService(userMgr, stockMgr, orderMgr, tradeMgr, orderBookMgr);

        // --- Seed data ---
        User alice = userService.createUser(new User("Alice", 10000));
        User bob = userService.createUser(new User("Bob", 10000));
        User charlie = userService.createUser(new User("Charlie", 10000));

        // Bob and Charlie start with some AAPL shares (so they can sell)
        bob.getHoldings().put("AAPL", 50);
        charlie.getHoldings().put("AAPL", 30);

        stockService.addStock(new Stock("AAPL", "Apple Inc"));

        // TODO: add demo scenarios once OrderService.match + executeTrade are filled in
        //
        // Suggested scenarios:
        //   1. Bob places SELL 50 @ 100 → sits in book (no buyers)
        //   2. Charlie places SELL 30 @ 102 → sits in book
        //   3. Alice places BUY 40 @ 101 → matches Bob, trade at $100
        //   4. Alice places BUY 25 @ 103 → partial fill across Bob's remaining + Charlie
        //   5. Cancel an open order
        //   6. Insufficient balance → throws
        //   7. Insufficient holdings on sell → throws
    }
}
