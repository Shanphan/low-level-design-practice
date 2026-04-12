package strategy;

import exception.InvalidPercentageSplitException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PercentageSplitStrategyImpl implements SplitStrategy {
    @Override
    public Map<String, Double> splits(double amount, Map<String, Double> splitDetails, Set<String> participants) {
        Map<String, Double> result = new HashMap<>();

        double total = splitDetails.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(total - 100.0) > 0.01) {
            throw new InvalidPercentageSplitException("Percentages sum to " + total + ", expected 100");
        }

        for (Map.Entry<String, Double> entry : splitDetails.entrySet()) {
            double share = Math.round(entry.getValue() / 100.0 * amount * 100.0) / 100.0;
            result.put(entry.getKey(), share);
        }

        return result;
    }
}
