package strategy;

import entity.ParkingSpot;

import java.util.List;

/**
 * Strategy interface for parking spot selection algorithms.
 * <p>
 * Different implementations can provide different selection logic:
 * - First available
 * - Nearest to entrance
 * - Random selection
 * - VIP/Reserved spots
 * <p>
 * This follows the Strategy Pattern, allowing algorithms to be
 * selected at runtime without changing client code.
 */
public interface SpotSelectionStrategy {

    /**
     * Find an available parking spot from the given list.
     *
     * @param availableSpots List of parking spots to choose from
     * @return Selected parking spot, or null if none suitable
     */

    ParkingSpot findSpot(List<ParkingSpot> availableSpots);

    /**
     * Get the name of this strategy for logging/display.
     */
    String getStrategyName();

}
