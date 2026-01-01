import java.time.LocalTime;

public class Entrance {

    private final ParkingSpotManager parkingSpotManager;
    private static Entrance instance;

    private Entrance(ParkingSpotManager parkingSpotManager) {
        this.parkingSpotManager = parkingSpotManager;
    }

    public static Entrance getInstance(ParkingSpotManager parkingSpotManager) {

        if(instance == null) {
            return new Entrance(parkingSpotManager);
        }

        return instance;
    }

    public ParkingSpot findAndUpdateParkingSpot(Vehicle vehicle) {

        //strategy First Empty Spot simple
        ParkingSpot parkingSpot = null;
        for(ParkingSpot ps : parkingSpotManager.getParkingSpots().get(vehicle.vehicleType)) {
            if(ps.isEmpty) {
                parkingSpot = ps;
                parkingSpot.parkVehicle(vehicle);
                break;
            }
        }

        if(parkingSpot == null) {
            throw new RuntimeException("NO Parking space available");
        }

        return parkingSpot;
    }

    public Ticket generateTicket(ParkingSpot parkingSpot) {
        return new Ticket(parkingSpot, LocalTime.now());
    }

}
