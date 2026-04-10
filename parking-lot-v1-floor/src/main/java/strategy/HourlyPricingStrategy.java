package strategy;

import entity.Ticket;
import enums.SpotType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class HourlyPricingStrategy implements PricingStrategy {

    private final Map<SpotType, Integer> ratePerHour;
    private final double dailyCapMultiplier;

    public HourlyPricingStrategy(Map<SpotType, Integer> ratePerHour) {
        this.ratePerHour = ratePerHour;
        this.dailyCapMultiplier = 8.0;
    }

    public HourlyPricingStrategy(Map<SpotType, Integer> ratePerHour, double dailyCapMultiplier) {
        this.ratePerHour = ratePerHour;
        this.dailyCapMultiplier = dailyCapMultiplier;
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        long minutesParked = ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime);

        long hoursParked = (minutesParked + 59) / 60;
        if (hoursParked < 1) {
            hoursParked = 1;
        }

        SpotType spotType = ticket.getParkingSpot().getParkingSpotType();
        int pricePerHour = ratePerHour.getOrDefault(spotType, 0);

        double dailyCap = pricePerHour * dailyCapMultiplier;

        long totalDays = hoursParked / 24;
        long remainingHours = hoursParked % 24;

        double totalPrice = (totalDays * dailyCap) + (remainingHours * pricePerHour);

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
