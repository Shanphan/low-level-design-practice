import java.time.LocalDateTime;

/**
 * Strategy interface for parking fee calculation algorithms.
 * <p>
 * Different implementations can provide different pricing models:
 * - Per-minute pricing
 * - Hourly with daily cap
 * - Flat rate
 * - Weekend vs weekday rates
 * - Peak hour pricing
 * <p>
 * This follows the Strategy Pattern, allowing pricing models to be
 * changed at runtime without modifying client code.
 */

public interface PricingStrategy {

    /**
     * Calculate parking fee based on ticket information.
     *
     * @param ticket The parking ticket
     * @param exitTime The time of exit
     * @return Calculated parking fee
     */
    double calculatePrice(Ticket ticket, LocalDateTime exitTime);

    /**
     * Get the name of this pricing strategy for logging/display.
     */
    String getStrategyName();

    /**
     * Get a description of how this pricing works.
     * Useful for displaying to customers.
     */
    String getDescription();


}
