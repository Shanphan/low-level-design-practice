import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a parking ticket issued when a vehicle enters.
 * <p>
 * ✅ Changed LocalTime to LocalDateTime - need date too!
 * ✅ Made fields final - ticket data shouldn't change
 * ✅ Removed setters - ticket is immutable once created
 * ✅ Added validation
 * ✅ Better toString() for printing
 */

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
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Ticket[ID: %s, Spot: %s, Entry: %s]",
                getId().toString().substring(0, 8),
                parkingSpot.getParkingSpotType().getDisplayName(),
                entryTime.format(formatter)
        );
    }

}


