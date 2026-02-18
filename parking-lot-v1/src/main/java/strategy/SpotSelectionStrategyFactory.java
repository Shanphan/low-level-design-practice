package strategy;

import enums.SpotSelectionType;

import java.util.Objects;

public class SpotSelectionStrategyFactory {

    public static SpotSelectionStrategy create(SpotSelectionType type) {
        if (Objects.requireNonNull(type) == SpotSelectionType.FARTHEST) {
            return new FarthestSpotStrategy();
        }
        return new NearestSpotStrategy();
    }
}
