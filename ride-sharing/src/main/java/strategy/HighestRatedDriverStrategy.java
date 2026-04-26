package strategy;

import entity.Driver;
import entity.Location;

import java.util.Comparator;
import java.util.List;

public class HighestRatedDriverStrategy implements MatchingStrategy {

    @Override
    public Driver match(Location pickup, List<Driver> availableDrivers) {
        return availableDrivers.stream()
                .max(Comparator.comparingDouble(Driver::getRating))
                .orElse(null);
    }
}
