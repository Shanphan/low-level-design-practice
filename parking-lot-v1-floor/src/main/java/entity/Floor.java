package entity;

import enums.SpotType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Floor extends Entity {

    private final String floorId;
    private final SpotType allowedType;
    private final List<ParkingSpot> spots;

    public Floor(String floorId, SpotType allowedType, int numSpots) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        if (allowedType == null) {
            throw new IllegalArgumentException("Allowed spot type cannot be null");
        }
        if (numSpots <= 0) {
            throw new IllegalArgumentException("Number of spots must be positive");
        }

        this.floorId = floorId;
        this.allowedType = allowedType;
        this.spots = new ArrayList<>();

        for (int i = 0; i < numSpots; i++) {
            spots.add(new ParkingSpot(allowedType, floorId));
        }
    }

    public int getAvailableSpotCount() {
        return (int) spots.stream().filter(ParkingSpot::isEmpty).count();
    }

    public int getTotalSpotCount() {
        return spots.size();
    }
}
