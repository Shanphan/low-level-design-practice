package manager;

import entity.Order;

import java.util.HashMap;
import java.util.Map;

public class OrderMgr {

    private final Map<String, Order> orders = new HashMap<>();

    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    public Order findById(String id) {
        return orders.get(id);
    }
}
