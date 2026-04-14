package service;

import entity.Order;
import entity.OrderBook;
import entity.OrderStatus;
import entity.OrderType;
import entity.Trade;
import entity.User;
import exception.InsufficientBalanceException;
import exception.InsufficientHoldingsException;
import exception.OrderNotFoundException;
import exception.StockNotFoundException;
import exception.UserNotFoundException;
import manager.OrderBookMgr;
import manager.OrderMgr;
import manager.StockMgr;
import manager.TradeMgr;
import manager.UserMgr;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class OrderService {

    private final UserMgr userMgr;
    private final StockMgr stockMgr;
    private final OrderMgr orderMgr;
    private final TradeMgr tradeMgr;
    private final OrderBookMgr orderBookMgr;

    public OrderService(UserMgr userMgr, StockMgr stockMgr, OrderMgr orderMgr,
                        TradeMgr tradeMgr, OrderBookMgr orderBookMgr) {
        this.userMgr = userMgr;
        this.stockMgr = stockMgr;
        this.orderMgr = orderMgr;
        this.tradeMgr = tradeMgr;
        this.orderBookMgr = orderBookMgr;
    }

    /**
     * Place a new order. Validates, then matches against the opposite book.
     * Any unmatched remainder rests in the book.
     */
    public Order placeOrder(String userId, String stockId, OrderType type, double price, int quantity) {
        // --- validations ---
        User user = userMgr.findById(userId);
        if (user == null) throw new UserNotFoundException("User not found: " + userId);
        if (!stockMgr.existsById(stockId)) throw new StockNotFoundException("Stock not found: " + stockId);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");

        if (type == OrderType.BUY) {
            double maxCost = price * quantity;
            if (user.getBalance() < maxCost) {
                throw new InsufficientBalanceException("Need " + maxCost + ", have " + user.getBalance());
            }
        } else {
            int owned = user.getHoldings().getOrDefault(stockId, 0);
            if (owned < quantity) {
                throw new InsufficientHoldingsException("Need " + quantity + " shares of " + stockId + ", have " + owned);
            }
        }

        // --- create order ---
        Order newOrder = new Order(userId, stockId, type, price, quantity);
        orderMgr.save(newOrder);

        // --- match ---
        OrderBook book = orderBookMgr.getOrCreate(stockId);
        match(newOrder, book);

        // --- rest unmatched remainder ---
        if (newOrder.getRemainingQuantity() > 0) {
            if (type == OrderType.BUY) {
                book.addBuyOrder(newOrder);
            } else {
                book.addSellOrder(newOrder);
            }
        }

        return newOrder;
    }

    /**
     * Core matching loop. Walks the opposite book while prices cross.
     * For each match: create Trade, transfer money + shares, update both orders.
     *
     * TODO: implement
     */
    private void match(Order newOrder, OrderBook book) {

        TreeMap<Double, Queue<Order>> oppositeBook =
                (newOrder.getType() == OrderType.BUY) ? book.getSellOrders() : book.getBuyOrders();

        while (newOrder.getRemainingQuantity() > 0) {
            Map.Entry<Double, Queue<Order>> bestMatchPrice = oppositeBook.firstEntry();
            if (bestMatchPrice == null) break;  // opposite book empty

            double bestPrice = bestMatchPrice.getKey();

            // prices cross?
            if (newOrder.getType() == OrderType.BUY && bestPrice > newOrder.getPrice()) break;
            if (newOrder.getType() == OrderType.SELL && bestPrice < newOrder.getPrice()) break;

            Queue<Order> queue = bestMatchPrice.getValue();
            Order resting = queue.peek();

            int tradeQty = Math.min(newOrder.getRemainingQuantity(), resting.getRemainingQuantity());
            double tradePrice = resting.getPrice();  // resting order got there first, gets its price

            Order buyOrder  = (newOrder.getType() == OrderType.BUY)  ? newOrder : resting;
            Order sellOrder = (newOrder.getType() == OrderType.SELL) ? newOrder : resting;

            executeTrade(buyOrder, sellOrder, tradeQty, tradePrice);

            // remove resting if fully filled
            if (resting.getRemainingQuantity() == 0) {
                queue.poll();
                if (queue.isEmpty()) {
                    oppositeBook.remove(bestPrice);
                }
            }
        }


    }

    private void match2Loops(Order newOrder, OrderBook book) {

        TreeMap<Double, Queue<Order>> oppositeSideOrders = newOrder.getType() == OrderType.BUY ?
                book.getSellOrders(): book.getBuyOrders();

        while(newOrder.getRemainingQuantity() > 0) {

            if (oppositeSideOrders.isEmpty()) break;
            Double orderPrice = oppositeSideOrders.firstKey();
            Double newOrderPrice = newOrder.getPrice();

            if(newOrder.getType() == OrderType.BUY && orderPrice > newOrderPrice) break;
            if(newOrder.getType() == OrderType.SELL && orderPrice < newOrderPrice) break;

            Queue<Order> orderQueue = oppositeSideOrders.get(orderPrice);

            while(!orderQueue.isEmpty() && newOrder.getRemainingQuantity() > 0) {

                Order firstOppositeOrder = orderQueue.peek();

                //f = 50 n = 10, f = 10, n = 10, f = 2, n = 10
                int firstOppQ = firstOppositeOrder.getRemainingQuantity();
                int newOrderQty = newOrder.getRemainingQuantity();

                int qty = Math.min(firstOppQ, newOrderQty);
                double price = firstOppositeOrder.getPrice();

                Order byuOrder = newOrder.getType() == OrderType.BUY ? newOrder : firstOppositeOrder;
                Order sellOrder = newOrder.getType() == OrderType.SELL ? newOrder : firstOppositeOrder;

                executeTrade(byuOrder, sellOrder, qty, price);

                if(firstOppositeOrder.getRemainingQuantity() == 0) {
                    orderQueue.poll();
                }
            }

            if(orderQueue.isEmpty()) {
                oppositeSideOrders.remove(orderPrice);
            }
        }

    }



    /**
     * Executes a matched trade:
     *   - Create + save Trade
     *   - Update balances (buyer pays, seller receives)
     *   - Update holdings (buyer +shares, seller -shares)
     *   - Update remainingQuantity + status on both orders
     *
     * TODO: implement
     */
    private void executeTrade(Order buyOrder, Order sellOrder, int qty, double price) {

        Trade trade = new Trade(buyOrder.getId(), sellOrder.getId(), buyOrder.getStockId(), price, qty);
        tradeMgr.save(trade);

        //1. Settle shares
        String stock = buyOrder.getStockId();
        User buyer = userMgr.findById(buyOrder.getUserId());
        User seller = userMgr.findById(sellOrder.getUserId());

        buyer.getHoldings().put(stock, buyer.getHoldings().getOrDefault(stock, 0) + qty);
        seller.getHoldings().put(stock, seller.getHoldings().getOrDefault(stock, 0) - qty);

        //2. Settle money
        buyer.setBalance(buyer.getBalance() - qty * price);
        seller.setBalance(seller.getBalance() + qty * price);

        //3. Decrement remaining quantity + update status on BOTH orders
        buyOrder.setRemainingQuantity(buyOrder.getRemainingQuantity() - qty);
        sellOrder.setRemainingQuantity(sellOrder.getRemainingQuantity() - qty);

        buyOrder.setStatus(buyOrder.getRemainingQuantity() == 0
                ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED);
        sellOrder.setStatus(sellOrder.getRemainingQuantity() == 0
                ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED);
    }

    /**
     * Cancel an open order. Removes from the book, marks CANCELLED.
     *
     * TODO: implement
     */
    public void cancelOrder(String orderId) {
        // Hint:
        //   Order o = orderMgr.findById(orderId);
        //   if (o == null) throw OrderNotFoundException
        //   if (o.status != OPEN && o.status != PARTIALLY_FILLED) throw IllegalState
        //   remove o from its side of the book
        //   mark o.status = CANCELLED
    }
}
