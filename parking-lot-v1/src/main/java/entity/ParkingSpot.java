package entity;

import enums.SpotType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class ParkingSpot extends Entity {

    private boolean isEmpty;
    private Vehicle vehicle;
    private final SpotType parkingSpotType;

    public ParkingSpot(SpotType parkingSpotType) {
        this.parkingSpotType = parkingSpotType;
        this.isEmpty = true;
        this.vehicle = null;
    }

    public void parkVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (!isEmpty) {
            throw new IllegalStateException("Parking spot is already occupied");
        }
        if (vehicle.getVehicleType() != this.parkingSpotType) {
            throw new IllegalArgumentException(
                    String.format("Cannot park %s in %s spot",
                            vehicle.getVehicleType(), this.parkingSpotType));
        }

        this.vehicle = vehicle;
        this.isEmpty = false;
    }

    public void removeVehicle() {
        if (this.isEmpty) {
            throw new IllegalStateException("Parking spot is already empty");
        }
        this.vehicle = null;
        this.isEmpty = true;
    }

    public int getPrice() {
        return parkingSpotType.getBasePrice();
    }
}
