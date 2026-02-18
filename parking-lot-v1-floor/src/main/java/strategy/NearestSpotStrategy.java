package strategy;

import entity.ParkingSpot;

import java.util.List;

public class NearestSpotStrategy implements SpotSelectionStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> availableSpots) {

        if (availableSpots == null || availableSpots.isEmpty()) {
            return null;
        }

        return availableSpots.stream()
                .filter(ParkingSpot::isEmpty)
                .findFirst()
                .orElse(null);

    }

    @Override
    public String getStrategyName() {
        return "NEAREST";
    }
}
