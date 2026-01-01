import java.time.LocalTime;

public class Ticket extends Entity {

    ParkingSpot parkingSpot;
    LocalTime time;

    public Ticket() {
    }

    public Ticket(ParkingSpot parkingSpot, LocalTime time) {
        this.parkingSpot = parkingSpot;
        this.time = time;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot (ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
