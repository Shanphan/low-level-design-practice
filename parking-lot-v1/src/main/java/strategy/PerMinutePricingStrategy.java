package strategy;

import entity.Ticket;
import enums.SpotType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class PerMinutePricingStrategy implements PricingStrategy {

    private final Map<SpotType, Integer> ratePerMinute;

    public PerMinutePricingStrategy(Map<SpotType, Integer> ratePerMinute) {
        this.ratePerMinute = ratePerMinute;
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        long minutesParked = ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime);

        if (minutesParked < 1) {
            minutesParked = 1;
        }

        SpotType spotType = ticket.getParkingSpot().getParkingSpotType();
        int rate = ratePerMinute.getOrDefault(spotType, 0);
        return minutesParked * rate;
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
