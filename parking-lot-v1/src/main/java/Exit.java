import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Exit {

    ParkingSpotManager parkingSpotManager;
    private static Exit instance;

    private Exit(ParkingSpotManager parkingSpotManager) {
        this.parkingSpotManager = parkingSpotManager;
    }

    public static Exit getInstance(ParkingSpotManager parkingSpotManager) {

        if(instance == null) {
            return new Exit(parkingSpotManager);
        }
        return instance;
    }

    public void freeParkingSpace(ParkingSpot parkingSpot) {
        parkingSpot.removeVehicle();
    }

    public double determinePrice(Ticket ticket) {
        return (ChronoUnit.MINUTES.between(ticket.getTime(), LocalTime.now().plusHours(1))) * ticket.parkingSpot.getPrice();
    }
}
