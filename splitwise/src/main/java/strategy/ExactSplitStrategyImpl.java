package strategy;

import exception.ExactSplitAmountMismatchException;
import exception.InvalidPercentageSplitException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExactSplitStrategyImpl implements SplitStrategy {
    @Override
    public Map<String, Double> splits(double amount, Map<String, Double> splitDetails, Set<String> participants) {
        double total = splitDetails.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(total - amount) > 0.01) {
            throw new ExactSplitAmountMismatchException("Split amounts sum to " + total + " but expense is " + amount);
        }

        return splitDetails;
    }
}
