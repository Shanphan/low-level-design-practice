package strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EqualSplitStrategyImpl implements SplitStrategy {
    @Override
    public Map<String, Double> splits(double amount, Map<String, Double> splitDetails, Set<String> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        int n = participants.size();
        double amountPerPerson = Math.round(amount / n * 100.0) / 100.0;

        Map<String, Double> result = new HashMap<>();
        for (String userId : participants) {
            result.put(userId, amountPerPerson);
        }

        return result;


    }
}
