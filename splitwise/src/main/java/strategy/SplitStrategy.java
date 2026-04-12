package strategy;

import java.util.Map;
import java.util.Set;

public interface SplitStrategy {

    Map<String, Double> splits (double amount, Map<String, Double> splitDetails, Set<String> participants);
}
