package entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class Ticket extends Entity {

    private final ParkingSpot parkingSpot;
    private final LocalDateTime entryTime;

    public Ticket(ParkingSpot parkingSpot, LocalDateTime entryTime) {
        if (parkingSpot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null");
        }
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null");
        }
        this.parkingSpot = parkingSpot;
        this.entryTime = entryTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Ticket[ID: %s, Spot: %s, Entry: %s]",
                getId().toString().substring(0, 8),
                parkingSpot.getParkingSpotType().getDisplayName(),
                entryTime.format(formatter));
    }
}
