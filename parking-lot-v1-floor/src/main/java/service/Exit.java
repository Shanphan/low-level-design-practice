package service;

import entity.Ticket;
import manager.ParkingSpotManager;
import strategy.PricingStrategy;

import java.time.LocalDateTime;
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
    // STRATEGY PATTERN: Pluggable pricing algorithm
    private PricingStrategy pricingStrategy;

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
     * SET THE PRICING STRATEGY at runtime!
     * This is the power of Strategy Pattern.
     */
    public void setPricingStrategy(PricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Pricing strategy cannot be null");
        }
        this.pricingStrategy = strategy;
        System.out.println("✓ Pricing strategy changed to: " + strategy.getStrategyName());
        System.out.println("  " + strategy.getDescription());
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
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

        // Step 1: Calculate price using CURRENT STRATEGY
        LocalDateTime exitTime = LocalDateTime.now();
        double totalPrice = pricingStrategy.calculatePrice(ticket, exitTime);

        // Step 2: Display duration
        long minutesParked = ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime);
        System.out.printf("Duration: %d minutes%n", minutesParked);
        System.out.printf("Pricing: %s%n", pricingStrategy.getStrategyName());

        // Step 3: Free the spot
        parkingSpotManager.freeSpot(ticket.getParkingSpot());

        System.out.printf("Exit processed. Total fee: ₹%.2f%n", totalPrice);

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
        return pricingStrategy.calculatePrice(ticket, LocalDateTime.now());
    }
}
