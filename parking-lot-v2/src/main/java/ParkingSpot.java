
/**
 * Represents a parking spot in the parking lot.
 * Base class for specific spot types.
 *
 * ✅ Made class abstract - no generic spots, only specific types
 * ✅ Removed price field - now comes from SpotType enum
 * ✅ Made parkingSpotType final
 * ✅ Added validation in parkVehicle() - checks vehicle type matches!
 * ✅ Added error handling - can't park in occupied spot
 * ✅ Fields properly private
 * ✅ Added getters
 */

public abstract class ParkingSpot extends Entity {

    private boolean isEmpty;
    private Vehicle vehicle;
    private final SpotType parkingSpotType;

    public ParkingSpot(SpotType parkingSpotType) {
        this.parkingSpotType = parkingSpotType;
        this.isEmpty = true;
        this.vehicle = null;
    }

    /**
     * Park a vehicle in this spot.
     * @throws IllegalStateException if spot is already occupied
     * @throws IllegalArgumentException if vehicle type doesn't match spot type
     */

    public void parkVehicle (Vehicle vehicle) {

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (!isEmpty) {
            throw new IllegalStateException("Parking spot is already occupied");
        }

        if (vehicle.getVehicleType() != this.parkingSpotType) {
            throw new IllegalArgumentException(
                    String.format("Cannot park %s in %s spot",
                            vehicle.getVehicleType(),
                            this.parkingSpotType)
            );
        }

        this.vehicle = vehicle;
        isEmpty = false;
    }

    /**
     * Remove vehicle from this spot.
     * @throws IllegalStateException if spot is already empty
     */
    public void removeVehicle () {

        if (this.isEmpty) {
            throw new IllegalStateException("Parking spot is already empty");
        }

        this.vehicle = null;
        this.isEmpty = true;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    /**
     * Get the hourly price for this spot type.
     */
    public int getPrice() {
        return parkingSpotType.getBasePrice();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public SpotType getParkingSpotType() {
        return parkingSpotType;
    }

    @Override
    public String toString() {
        return String.format("ParkingSpot[%s, %s, %s]",
                getId(),
                parkingSpotType.getDisplayName(),
                isEmpty ? "Empty" : "Occupied by " + vehicle.getVehicleNo()
        );
    }
}
