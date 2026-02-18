package strategy;

import enums.PricingType;
import enums.SpotType;

import java.util.Map;

public class PricingStrategyFactory {

    public static PricingStrategy create(PricingType type, Map<SpotType, Integer> rates) {
        return switch (type) {
            case PER_MINUTE -> new PerMinutePricingStrategy(rates);
            case HOURLY -> new HourlyPricingStrategy(rates);
        };
    }
}
