public class Vehicle extends Entity {

    SpotType vehicleType;
    String vehicleNo;

    public Vehicle(String vehicleNo, SpotType vehicleType) {
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }
}
