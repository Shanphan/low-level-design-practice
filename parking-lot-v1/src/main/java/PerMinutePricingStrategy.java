import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Simple per-minute pricing strategy.
 * <p>
 * Pricing: minutes × price_per_minute
 * Minimum charge: 1 minute
 * <p>
 * Use case: Short-term parking, quick stops
 */

public class PerMinutePricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        LocalDateTime entryTime = ticket.getEntryTime();

        long minutesParked = ChronoUnit.MINUTES.between(entryTime, exitTime);

        // Minimum 1 minute charge
        if (minutesParked < 1) {
            minutesParked = 1;
        }

        int pricePerMinute = ticket.getParkingSpot().getPrice();
        return minutesParked * pricePerMinute;
    }

    @Override
    public String getStrategyName() {
        return "Per-Minute Pricing";
    }

    @Override
    public String getDescription() {
        return "Charged by the minute. Minimum 1 minute.";
    }
}
