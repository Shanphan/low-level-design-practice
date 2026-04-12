package strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EqualSplitStrategyImpl implements SplitStrategy {
    @Override
    public Map<String, Double> splits(double amount, Map<String, Double> splitDetails, Set<String> participants) {

        int n = participants.size();
        double amountPerPerson = amount/n;
        amountPerPerson = Math.round(amountPerPerson * 100.0) / 100.0;

        Map<String, Double> result = new HashMap<>();
        for(String userId : participants) {
            result.put(userId, amountPerPerson);
        }

        return result;


    }
}
