import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Singleton class representing the parking lot entrance.
 * Responsible for:
 *   1. Finding available parking spots (via Manager)
 *   2. Issuing tickets to vehicles
 *
 * NOTE: Entrance does NOT manage parking spots directly.
 * It USES ParkingSpotManager for all spot operations.
 */

public class Entrance {

    private final ParkingSpotManager parkingSpotManager;
    private static Entrance instance;

    private Entrance(ParkingSpotManager parkingSpotManager) {
        if (parkingSpotManager == null) {
            throw new IllegalArgumentException("ParkingSpotManager cannot be null");
        }
        this.parkingSpotManager = parkingSpotManager;
    }

    /**
     * Thread-safe singleton instance retrieval.
     */
    public static synchronized Entrance getInstance(ParkingSpotManager parkingSpotManager) {
        if (instance == null) {
            instance = new Entrance(parkingSpotManager);  // FIX: Actually assign!
        }
        return instance;
    }

    /**
     * Main entry point: Park a vehicle and issue ticket.
     * This method ORCHESTRATES the parking process.
     *
     * @param vehicle The vehicle to park
     * @return Ticket for the parked vehicle
     * @throws RuntimeException if no parking space available
     */

    public Ticket parkVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        System.out.println("\n--- Vehicle Entry ---");
        System.out.println("Vehicle: " + vehicle);

        // Step 1: Ask Manager to find available spot
        ParkingSpot availableSpot = parkingSpotManager.findAvailableSpot(vehicle.getVehicleType());

        if (availableSpot == null) {
            System.out.println("No parking space available for " + vehicle.getVehicleType().getDisplayName());
            throw new RuntimeException("No parking space available for " + vehicle.getVehicleType().getDisplayName());
        }

        // Step 2: Ask Manager to occupy the spot
        parkingSpotManager.occupySpot(availableSpot, vehicle);

        // Step 3: Generate ticket
        Ticket ticket = generateTicket(availableSpot);

        System.out.println("✓ Parked successfully!");
        System.out.println(ticket);

        return ticket;
    }



    public ParkingSpot findAndUpdateParkingSpot(Vehicle vehicle) {

        //strategy First Empty Spot simple
        ParkingSpot parkingSpot = null;
        for(ParkingSpot ps : parkingSpotManager.getParkingSpots().get(vehicle.vehicleType)) {
            if(ps.isEmpty()) {
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

    /**
     * Generate a parking ticket.
     * Private method - only Entrance creates tickets.
     */
    private Ticket generateTicket(ParkingSpot parkingSpot) {
        return new Ticket(parkingSpot, LocalDateTime.now());
    }

    /**
     * Check if parking is available for a vehicle type.
     * Useful for pre-entry checks or display boards.
     */
    public boolean isParkingAvailable(SpotType spotType) {
        return parkingSpotManager.getAvailableSpotCount(spotType) > 0;
    }

}
