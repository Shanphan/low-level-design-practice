package manager;

import entity.ParkingSpot;
import entity.Vehicle;
import enums.SpotSelectionType;
import enums.SpotType;
import strategy.SpotSelectionStrategy;
import strategy.SpotSelectionStrategyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSpotManager {

    private Map<SpotType, List<ParkingSpot>> parkingSpots;
    private static ParkingSpotManager instance;
    private SpotSelectionStrategy spotSelectionStrategy;

    private ParkingSpotManager() {
        this.parkingSpots = new HashMap<>();
        this.spotSelectionStrategy = SpotSelectionStrategyFactory.create(SpotSelectionType.NEAREST);
        // Initialize empty lists for each spot type
        for (SpotType type : SpotType.values()) {
            parkingSpots.put(type, new ArrayList<>());
        }

    }

    /**
     * Thread-safe singleton instance retrieval.
     */
    public static synchronized ParkingSpotManager getInstance() {
        if (instance == null) {
            instance = new ParkingSpotManager();  // FIX: Actually assign to instance!
        }
        return instance;
    }
    /**
     * Create initial parking spots for the parking lot.
     * @param numTwoWheeler Number of two-wheeler spots
     * @param numFourWheeler Number of four-wheeler spots
     */
    public void createParkingSpots(int numTwoWheeler, int numFourWheeler) {
        if (numTwoWheeler < 0 || numFourWheeler < 0) {
            throw new IllegalArgumentException("Number of spots cannot be negative");
        }

        for (int i = 0; i < numTwoWheeler; i++) {
            parkingSpots.get(SpotType.TWO_WHEELER).add(new ParkingSpot(SpotType.TWO_WHEELER));
        }

        for (int i = 0; i < numFourWheeler; i++) {
            parkingSpots.get(SpotType.FOUR_WHEELER).add(new ParkingSpot(SpotType.FOUR_WHEELER));
        }

        System.out.println("Parking lot initialized:");
        System.out.println("  - Two-wheeler spots: " + numTwoWheeler);
        System.out.println("  - Four-wheeler spots: " + numFourWheeler);
        System.out.println("  - Selection strategy: " + spotSelectionStrategy.getStrategyName());
    }

    /**
     * Add a new parking spot of specified type.
     * Shows how EASY it is to extend - no code changes needed for new types!
     */
    public void addParkingSpot(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }

        parkingSpots.get(spotType).add(new ParkingSpot(spotType));
        System.out.println("Added new " + spotType.getDisplayName() + " spot");
    }

    /**
     * Find an available parking spot for given vehicle type.
     * Returns null if no spot available.
     *
     * NOTE: This uses "First Available" strategy by default.
     * Later we'll extract this to a Strategy pattern!
     */

    public ParkingSpot findAvailableSpot(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }

        List<ParkingSpot> spots = parkingSpots.get(spotType);

       //DELEGATE TO STRATEGY
        return spotSelectionStrategy.findSpot(spots);
    }

    /**
     * Occupy a parking spot with a vehicle.
     * Manager coordinates this action.
     */
    public void occupySpot(ParkingSpot spot, Vehicle vehicle) {
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        spot.parkVehicle(vehicle);  // ParkingSpot handles validation
    }

    /**
     * Free up a parking spot.
     * Manager coordinates this action.
     */
    public void freeSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null");
        }

        spot.removeVehicle();
    }

    /**
     * Get count of available spots for a specific type.
     */
    public int getAvailableSpotCount(SpotType spotType) {
        return (int) parkingSpots.get(spotType).stream()
                .filter(ParkingSpot::isEmpty)
                .count();
    }

    /**
     * Get total spots for a specific type.
     */
    public int getTotalSpotCount(SpotType spotType) {
        return parkingSpots.get(spotType).size();
    }

    /**
     * Display current parking lot status.
     */
    public void displayStatus() {
        System.out.println("\n=== Parking Lot Status ===");
        for (SpotType type : SpotType.values()) {
            int total = getTotalSpotCount(type);
            int available = getAvailableSpotCount(type);
            int occupied = total - available;

            System.out.printf("%s: %d/%d occupied (%.1f%% full)%n",
                    type.getDisplayName(),
                    occupied,
                    total,
                    total > 0 ? (occupied * 100.0 / total) : 0
            );
        }
        System.out.println("==========================\n");
    }

}
