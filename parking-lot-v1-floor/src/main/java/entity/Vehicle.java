package entity;

import enums.SpotType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Vehicle extends Entity {

    private final SpotType vehicleType;
    private final String vehicleNo;

    public Vehicle(String vehicleNo, SpotType vehicleType) {
        if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        this.vehicleNo = vehicleNo.trim().toUpperCase();
        this.vehicleType = vehicleType;
    }
}
