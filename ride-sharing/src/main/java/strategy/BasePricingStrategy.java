package strategy;

import entity.Location;

public class BasePricingStrategy implements PricingStrategy {

    private static final double BASE_FARE = 50.0;
    private static final double PER_UNIT_DISTANCE = 10.0;

    @Override
    public double calculateFare(Location pickup, Location dropoff) {
        double distance = pickup.distanceTo(dropoff);
        return BASE_FARE + distance * PER_UNIT_DISTANCE;
    }
}
