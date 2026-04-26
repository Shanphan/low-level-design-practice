package service;

import entity.*;
import exception.*;
import manager.DriverMgr;
import manager.RideMgr;
import manager.RiderMgr;
import strategy.MatchingStrategy;
import strategy.PricingStrategy;

import java.util.List;

public class RideService {

    private final RideMgr rideMgr;
    private final RiderMgr riderMgr;
    private final DriverMgr driverMgr;
    private final MatchingStrategy matchingStrategy;
    private final PricingStrategy pricingStrategy;

    public RideService(RideMgr rideMgr, RiderMgr riderMgr, DriverMgr driverMgr,
                       MatchingStrategy matchingStrategy, PricingStrategy pricingStrategy) {
        this.rideMgr = rideMgr;
        this.riderMgr = riderMgr;
        this.driverMgr = driverMgr;
        this.matchingStrategy = matchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public Ride requestRide(String riderId, Location pickup, Location dropoff) {
        Rider rider = riderMgr.findById(riderId);
        if (rider == null) throw new RiderNotFoundException("Rider not found: " + riderId);

        List<Driver> available = driverMgr.findAvailable();
        if (available.isEmpty()) throw new NoDriverAvailableException("No drivers available");

        Driver matched = matchingStrategy.match(pickup, available);
        if (matched == null) throw new NoDriverAvailableException("No matching driver found");

        Ride ride = new Ride(riderId, matched.getId(), pickup, dropoff);
        rideMgr.save(ride);

        matched.setStatus(DriverStatus.ON_TRIP);

        return ride;
    }

    public Ride acceptRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        transitionStatus(ride, RideStatus.ACCEPTED);
        return ride;
    }

    public Ride startRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        transitionStatus(ride, RideStatus.IN_PROGRESS);
        return ride;
    }

    public Ride completeRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        transitionStatus(ride, RideStatus.COMPLETED);

        double fare = pricingStrategy.calculateFare(ride.getPickup(), ride.getDropoff());
        ride.setFare(Math.round(fare * 100.0) / 100.0);

        Driver driver = driverMgr.findById(ride.getDriverId());
        driver.setStatus(DriverStatus.AVAILABLE);

        return ride;
    }

    public Ride cancelRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        transitionStatus(ride, RideStatus.CANCELLED);

        Driver driver = driverMgr.findById(ride.getDriverId());
        driver.setStatus(DriverStatus.AVAILABLE);

        return ride;
    }

    public void rateDriver(String rideId, double score) {
        if (score < 1 || score > 5) throw new IllegalArgumentException("Rating must be 1-5");
        Ride ride = getRideOrThrow(rideId);
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new InvalidRideStateException("Can only rate after ride is completed");
        }
        Driver driver = driverMgr.findById(ride.getDriverId());
        driver.addRating(score);
    }

    public void rateRider(String rideId, double score) {
        if (score < 1 || score > 5) throw new IllegalArgumentException("Rating must be 1-5");
        Ride ride = getRideOrThrow(rideId);
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new InvalidRideStateException("Can only rate after ride is completed");
        }
        Rider rider = riderMgr.findById(ride.getRiderId());
        rider.addRating(score);
    }

    public List<Ride> getRideHistory(String riderId) {
        return rideMgr.findByRiderId(riderId);
    }

    private Ride getRideOrThrow(String rideId) {
        Ride ride = rideMgr.findById(rideId);
        if (ride == null) throw new RideNotFoundException("Ride not found: " + rideId);
        return ride;
    }

    private void transitionStatus(Ride ride, RideStatus next) {
        if (!ride.getStatus().canTransitionTo(next)) {
            throw new InvalidRideStateException(
                    "Cannot transition from " + ride.getStatus() + " to " + next);
        }
        ride.setStatus(next);
    }
}
