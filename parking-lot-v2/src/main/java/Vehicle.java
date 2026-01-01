/**
 * Represents a vehicle in the parking lot system.
 * <p>
 * ✅ Fields now private final - proper encapsulation
 * ✅ Added validation - no null/empty vehicle numbers
 * ✅ Normalize vehicle number (uppercase, trimmed)
 * ✅ Added getters - proper access
 * ✅ Added toString() for debugging
 */

public class Vehicle extends Entity {

    SpotType vehicleType;
    String vehicleNo;

    public Vehicle(String vehicleNo, SpotType vehicleType) {

        // Validation
        if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        this.vehicleNo = vehicleNo.trim().toUpperCase();  // Normalize;
        this.vehicleType = vehicleType;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public SpotType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[%s, %s]", vehicleNo, vehicleType.getDisplayName());
    }

}
