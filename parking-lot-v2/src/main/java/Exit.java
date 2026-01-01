import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Singleton class representing the parking lot exit.
 * Responsible for:
 *   1. Calculating parking fees
 *   2. Processing vehicle exit
 *   3. Freeing parking spots (via Manager)
 *
 * NOTE: Exit does NOT manage parking spots directly.
 * It USES ParkingSpotManager for all spot operations.
 */

public class Exit {

    private final ParkingSpotManager parkingSpotManager;
    private static Exit instance;

    private Exit(ParkingSpotManager parkingSpotManager) {
        if (parkingSpotManager == null) {
            throw new IllegalArgumentException("ParkingSpotManager cannot be null");
        }
        this.parkingSpotManager = parkingSpotManager;
    }

    /**
     * Thread-safe singleton instance retrieval.
     */
    public static synchronized Exit getInstance(ParkingSpotManager parkingSpotManager) {
        if (instance == null) {
            instance = new Exit(parkingSpotManager);  // FIX: Actually assign!
        }
        return instance;
    }

    /**
     * Main exit point: Process vehicle exit and calculate fee.
     * This method ORCHESTRATES the exit process.
     *
     * @param ticket The parking ticket
     * @return Total parking fee
     */
    public double processExit(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }

        System.out.println("\n--- Vehicle Exit ---");
        System.out.println(ticket);

        // Step 1: Calculate price
        double totalPrice = calculatePrice(ticket);

        // Step 2: Ask Manager to free the spot
        parkingSpotManager.freeSpot(ticket.getParkingSpot());

        System.out.printf("✓ Exit processed. Total fee: ₹%.2f%n", totalPrice);

        return totalPrice;
    }

    /**
     * Calculate parking fee based on duration.
     *
     * Pricing logic:
     * - Price per minute = spot's base price (from SpotType enum)
     * - Total = minutes parked × price per minute
     *
     * NOTE: In real systems, you'd have more complex pricing:
     *       - First hour free
     *       - Hourly rates with daily caps
     *       - Weekend vs weekday rates
     *       Later we'll use PricingStrategy pattern for this!
     */
    private double calculatePrice(Ticket ticket) {
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();

        long minutesParked = ChronoUnit.MINUTES.between(entryTime, exitTime);

        // Minimum 1 minute charge
        if (minutesParked < 1) {
            minutesParked = 1;
        }

        int pricePerMinute = ticket.getParkingSpot().getPrice();
        double totalPrice = minutesParked * pricePerMinute;

        // Display breakdown
        System.out.printf("Duration: %d minutes @ ₹%d/min%n", minutesParked, pricePerMinute);

        return totalPrice;
    }

    /**
     * Calculate price without processing exit.
     * Useful for "Check Fee" functionality at payment kiosks.
     */
    public double checkPrice(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        return calculatePrice(ticket);
    }
}
