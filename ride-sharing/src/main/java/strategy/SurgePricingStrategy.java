package strategy;

import entity.Location;

public class SurgePricingStrategy implements PricingStrategy {

    private final double surgeMultiplier;
    private final BasePricingStrategy basePricing = new BasePricingStrategy();

    public SurgePricingStrategy(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateFare(Location pickup, Location dropoff) {
        return basePricing.calculateFare(pickup, dropoff) * surgeMultiplier;
    }
}
