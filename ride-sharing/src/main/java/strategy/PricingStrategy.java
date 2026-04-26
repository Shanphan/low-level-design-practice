package strategy;

import entity.Location;

public interface PricingStrategy {
    double calculateFare(Location pickup, Location dropoff);
}
