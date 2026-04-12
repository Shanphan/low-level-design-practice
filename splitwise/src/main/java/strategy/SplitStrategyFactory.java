package strategy;

import entity.SplitType;

public class SplitStrategyFactory {

    public static SplitStrategy createStrategy (SplitType splitType) {

        switch (splitType) {
            case PERCENTAGE -> {
                return new PercentageSplitStrategyImpl();
            }
            case EXACT -> {
                return new ExactSplitStrategyImpl();
            }
            default -> {
                return new EqualSplitStrategyImpl();
            }
        }
    }
}
