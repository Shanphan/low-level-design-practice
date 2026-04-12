package manager;

import java.util.HashMap;
import java.util.Map;

public class BalanceMgr {

    private Map<String, Map<String, Double>> balanceSheet;

    public BalanceMgr() {
        this.balanceSheet = new HashMap<>();
    }

    public void update(String userA, String userB, double amount) {
        // balances[userA][userB] += amount A -> [b, 300] b se 300 lena he
        // balances[userB][userA] -= amount b -> [a, -300] a ko 300 dena he

        balanceSheet.computeIfAbsent(userA, k -> new HashMap<>());
        Map<String, Double> userAMap = balanceSheet.get(userA);
        userAMap.put(userB, userAMap.getOrDefault(userB, 0.0) + amount);

        balanceSheet.computeIfAbsent(userB, k -> new HashMap<>());
        Map<String, Double> userBMap = balanceSheet.get(userB);
        userBMap.put(userA, userBMap.getOrDefault(userA, 0.0) - amount);

    }


    public Map<String, Double> getBalancesForUser(String userId) {
        return balanceSheet.getOrDefault(userId, new HashMap<>());
    }


}
