import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HourlyPricingStrategy implements PricingStrategy {

    private final int hoursPerDay;
    private final double dailyCapMultiplier;

    public HourlyPricingStrategy() {
        this.hoursPerDay = 24;
        this.dailyCapMultiplier = 8.0;  // Daily cap = 8 hours worth;
    }

    public HourlyPricingStrategy(int hoursPerDay, double dailyCapMultiplier) {
        this.hoursPerDay = hoursPerDay;
        this.dailyCapMultiplier = dailyCapMultiplier;
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        LocalDateTime entryTime = ticket.getEntryTime();

        long minutesParked = ChronoUnit.MINUTES.between(entryTime, exitTime);

        // Convert to hours (round up)
        long hoursParked = (minutesParked + 59) / 60;  // Ceiling division

        // Minimum 1 hour charge
        if (hoursParked < 1) {
            hoursParked = 1;
        }

        int pricePerMinute = ticket.getParkingSpot().getPrice();
        int pricePerHour = pricePerMinute * 60;

        // Calculate daily cap
        double dailyCap = pricePerHour * dailyCapMultiplier;

        // Calculate total days
        long totalDays = hoursParked / hoursPerDay;
        long remainingHours = hoursParked % hoursPerDay;

        // Total price with daily cap
        double totalPrice = (totalDays * dailyCap) + (remainingHours * pricePerHour);

        // Apply cap if remaining hours exceed it
        if (remainingHours * pricePerHour > dailyCap) {
            totalPrice = (totalDays + 1) * dailyCap;
        }

        return totalPrice;
    }

    private double getTotalPrice(int pricePerMinute, long hoursParked) {
        int pricePerHour = pricePerMinute * 60;

        // Calculate daily cap
        double dailyCap = pricePerHour * dailyCapMultiplier;

        // Calculate total days
        long totalDays = hoursParked / hoursPerDay;
        long remainingHours = hoursParked % hoursPerDay;

        // Total price with daily cap
        double totalPrice = (totalDays * dailyCap) + (remainingHours * pricePerHour);

        // Apply cap if remaining hours exceed it
        if (remainingHours * pricePerHour > dailyCap) {
            totalPrice = (totalDays + 1) * dailyCap;
        }
        return totalPrice;
    }

    @Override
    public String getStrategyName() {
        return "Hourly Pricing with Daily Cap";
    }

    @Override
    public String getDescription() {
        return String.format("Charged per hour (rounded up). Daily maximum: %.0f hours worth.",
                dailyCapMultiplier);
    }
}
