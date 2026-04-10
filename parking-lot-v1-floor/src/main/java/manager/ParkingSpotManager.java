package manager;

import entity.Floor;
import entity.ParkingSpot;
import entity.Vehicle;
import enums.SpotSelectionType;
import enums.SpotType;
import lombok.Getter;
import lombok.Setter;
import strategy.SpotSelectionStrategy;
import strategy.SpotSelectionStrategyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ParkingSpotManager {

    private Map<SpotType, List<Floor>> floorsByType;
    private static ParkingSpotManager instance;
    private SpotSelectionStrategy spotSelectionStrategy;

    private ParkingSpotManager() {
        this.floorsByType = new HashMap<>();
        this.spotSelectionStrategy = SpotSelectionStrategyFactory.create(SpotSelectionType.NEAREST);
        for (SpotType type : SpotType.values()) {
            floorsByType.put(type, new ArrayList<>());
        }
    }

    public static synchronized ParkingSpotManager getInstance() {
        if (instance == null) {
            instance = new ParkingSpotManager();
        }
        return instance;
    }

    /**
     * Add a floor to the parking lot.
     * The floor already knows its SpotType and how many spots it has.
     */
    public void addFloor(Floor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }
        floorsByType.get(floor.getAllowedType()).add(floor);
        System.out.println("Added floor " + floor.getFloorId()
                + " (" + floor.getAllowedType().getDisplayName()
                + ", " + floor.getTotalSpotCount() + " spots)");
    }

    /**
     * Find an available spot for the given vehicle type.
     * Iterates floors in registration order; delegates spot selection to the strategy.
     * Strategy interface is unchanged — it still receives List<ParkingSpot> per floor.
     */
    public ParkingSpot findAvailableSpot(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }

        for (Floor floor : floorsByType.get(spotType)) {
            ParkingSpot spot = spotSelectionStrategy.findSpot(floor.getSpots());
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public void occupySpot(ParkingSpot spot, Vehicle vehicle) {
        if (spot == null) throw new IllegalArgumentException("Parking spot cannot be null");
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null");
        spot.parkVehicle(vehicle);
    }

    public void freeSpot(ParkingSpot spot) {
        if (spot == null) throw new IllegalArgumentException("Parking spot cannot be null");
        spot.removeVehicle();
    }

    public int getAvailableSpotCount(SpotType spotType) {
        return floorsByType.get(spotType).stream()
                .mapToInt(Floor::getAvailableSpotCount)
                .sum();
    }

    public int getTotalSpotCount(SpotType spotType) {
        return floorsByType.get(spotType).stream()
                .mapToInt(Floor::getTotalSpotCount)
                .sum();
    }

    public void displayStatus() {
        System.out.println("\n=== Parking Lot Status ===");
        for (SpotType type : SpotType.values()) {
            System.out.println(type.getDisplayName() + ":");
            for (Floor floor : floorsByType.get(type)) {
                int total = floor.getTotalSpotCount();
                int available = floor.getAvailableSpotCount();
                int occupied = total - available;
                System.out.printf("  %s: %d/%d occupied (%.1f%% full)%n",
                        floor.getFloorId(),
                        occupied,
                        total,
                        total > 0 ? (occupied * 100.0 / total) : 0
                );
            }
        }
        System.out.println("==========================\n");
    }
}
