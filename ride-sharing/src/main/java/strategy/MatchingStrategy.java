package strategy;

import entity.Driver;
import entity.Location;

import java.util.List;

public interface MatchingStrategy {
    Driver match(Location pickup, List<Driver> availableDrivers);
}
