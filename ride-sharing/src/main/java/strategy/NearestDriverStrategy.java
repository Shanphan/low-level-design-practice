package strategy;

import entity.Driver;
import entity.Location;

import java.util.Comparator;
import java.util.List;

public class NearestDriverStrategy implements MatchingStrategy {

    @Override
    public Driver match(Location pickup, List<Driver> availableDrivers) {
        return availableDrivers.stream()
                .min(Comparator.comparingDouble(d -> d.getLocation().distanceTo(pickup)))
                .orElse(null);
    }
}
