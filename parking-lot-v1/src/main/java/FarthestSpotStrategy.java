import java.util.List;

/**
 * Selects the parking spot farthest from entrance (last in list).
 * Saves closer spots for later arrivals or quick stops.
 *
 * Advantages:
 * - Distributes wear evenly across parking lot
 * - Keeps spots near entrance available for disabled/elderly
 * - Reduces congestion near entrance
 *
 * Use case:
 * - Airport long-term parking
 * - Employee parking (save close spots for customers)
 */
public class FarthestSpotStrategy implements SpotSelectionStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> availableSpots) {

        if (availableSpots == null || availableSpots.isEmpty()) {
            return null;
        }

        for(int i = availableSpots.size() - 1; i >= 0; i--) {
            ParkingSpot ps = availableSpots.get(i);
            if(ps.isEmpty()) {
                return ps;
            }
        }
        return null;
    }

    @Override
    public String getStrategyName() {
        return "FARTHEST - AVAIL";
    }
}
