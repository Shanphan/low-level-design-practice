import entity.*;
import manager.*;
import service.*;
import strategy.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // --- Wire dependencies ---
        RiderMgr riderMgr = new RiderMgr();
        DriverMgr driverMgr = new DriverMgr();
        RideMgr rideMgr = new RideMgr();

        RiderService riderService = new RiderService(riderMgr);
        DriverService driverService = new DriverService(driverMgr);

        MatchingStrategy matchingStrategy = new NearestDriverStrategy();
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        RideService rideService = new RideService(rideMgr, riderMgr, driverMgr, matchingStrategy, pricingStrategy);

        // --- Create riders ---
        Rider alice = riderService.register(new Rider("Alice", new Location(12.97, 77.59)));
        Rider bob = riderService.register(new Rider("Bob", new Location(12.93, 77.63)));

        System.out.println("=== Riders Registered ===");
        System.out.println(alice);
        System.out.println(bob);

        // --- Create drivers ---
        Driver raju = driverService.register(new Driver("Raju", new Location(12.96, 77.58)));
        Driver kumar = driverService.register(new Driver("Kumar", new Location(12.99, 77.61)));
        Driver sita = driverService.register(new Driver("Sita", new Location(12.90, 77.55)));

        driverService.goOnline(raju.getId());
        driverService.goOnline(kumar.getId());
        driverService.goOnline(sita.getId());

        System.out.println("\n=== Drivers Online ===");
        System.out.println(raju);
        System.out.println(kumar);
        System.out.println(sita);

        // --- Scenario 1: Alice requests ride — nearest driver matched ---
        System.out.println("\n=== Scenario 1: Alice requests ride (nearest driver) ===");
        Location alicePickup = new Location(12.97, 77.59);
        Location aliceDropoff = new Location(13.03, 77.65);
        Ride ride1 = rideService.requestRide(alice.getId(), alicePickup, aliceDropoff);
        System.out.println("Ride created: " + ride1);
        System.out.println("Matched driver: " + driverMgr.findById(ride1.getDriverId()));

        // --- Scenario 2: Full ride lifecycle ---
        System.out.println("\n=== Scenario 2: Accept → Start → Complete ===");
        rideService.acceptRide(ride1.getId());
        System.out.println("Accepted: " + ride1.getStatus());

        rideService.startRide(ride1.getId());
        System.out.println("Started: " + ride1.getStatus());

        rideService.completeRide(ride1.getId());
        System.out.println("Completed: " + ride1);
        System.out.println("Driver status: " + driverMgr.findById(ride1.getDriverId()).getStatus());

        // --- Scenario 3: Rate driver and rider ---
        System.out.println("\n=== Scenario 3: Rate driver and rider ===");
        rideService.rateDriver(ride1.getId(), 4.5);
        rideService.rateRider(ride1.getId(), 5.0);
        System.out.println("Driver after rating: " + driverMgr.findById(ride1.getDriverId()));
        System.out.println("Rider after rating: " + alice);

        // --- Scenario 4: Bob requests ride — driver already on trip ---
        System.out.println("\n=== Scenario 4: Bob requests ride (fewer drivers available) ===");
        Ride ride2 = rideService.requestRide(bob.getId(), new Location(12.93, 77.63), new Location(12.98, 77.60));
        System.out.println("Ride created: " + ride2);
        System.out.println("Matched driver: " + driverMgr.findById(ride2.getDriverId()));

        // --- Scenario 5: Cancel ride ---
        System.out.println("\n=== Scenario 5: Cancel ride ===");
        rideService.cancelRide(ride2.getId());
        System.out.println("Cancelled: " + ride2);
        System.out.println("Driver status: " + driverMgr.findById(ride2.getDriverId()).getStatus());

        // --- Scenario 6: Surge pricing ---
        System.out.println("\n=== Scenario 6: Surge pricing (2x) ===");
        PricingStrategy surgeStrategy = new SurgePricingStrategy(2.0);
        RideService surgeRideService = new RideService(rideMgr, riderMgr, driverMgr, matchingStrategy, surgeStrategy);
        Ride ride3 = surgeRideService.requestRide(alice.getId(), alicePickup, aliceDropoff);
        surgeRideService.acceptRide(ride3.getId());
        surgeRideService.startRide(ride3.getId());
        surgeRideService.completeRide(ride3.getId());
        System.out.println("Surge ride: " + ride3);
        System.out.println("Normal fare was: " + ride1.getFare() + ", Surge fare: " + ride3.getFare());

        // --- Scenario 7: Highest rated matching ---
        System.out.println("\n=== Scenario 7: Highest rated driver matching ===");
        MatchingStrategy ratedStrategy = new HighestRatedDriverStrategy();
        RideService ratedRideService = new RideService(rideMgr, riderMgr, driverMgr, ratedStrategy, pricingStrategy);
        Ride ride4 = ratedRideService.requestRide(bob.getId(), new Location(12.93, 77.63), new Location(12.98, 77.60));
        System.out.println("Matched driver (highest rated): " + driverMgr.findById(ride4.getDriverId()));

        ratedRideService.acceptRide(ride4.getId());
        ratedRideService.startRide(ride4.getId());
        ratedRideService.completeRide(ride4.getId());

        // --- Scenario 8: Ride history ---
        System.out.println("\n=== Scenario 8: Alice's ride history ===");
        List<Ride> history = rideService.getRideHistory(alice.getId());
        for (Ride r : history) {
            System.out.println("  " + r);
        }

        // --- Scenario 9: No drivers available ---
        System.out.println("\n=== Scenario 9: No drivers available (all offline) ===");
        driverService.goOffline(raju.getId());
        driverService.goOffline(kumar.getId());
        driverService.goOffline(sita.getId());
        try {
            rideService.requestRide(alice.getId(), alicePickup, aliceDropoff);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 10: Invalid state transition ---
        System.out.println("\n=== Scenario 10: Invalid state transition (complete a cancelled ride) ===");
        try {
            rideService.completeRide(ride2.getId());
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 11: Cannot go offline while on trip ---
        System.out.println("\n=== Scenario 11: Cannot go offline while on trip ===");
        driverService.goOnline(raju.getId());
        Ride ride5 = rideService.requestRide(bob.getId(), new Location(12.93, 77.63), new Location(12.98, 77.60));
        try {
            driverService.goOffline(raju.getId());
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }
        rideService.acceptRide(ride5.getId());
        rideService.startRide(ride5.getId());
        rideService.completeRide(ride5.getId());

        // --- Scenario 12: Rate before completion ---
        System.out.println("\n=== Scenario 12: Rate before completion (should throw) ===");
        driverService.goOnline(kumar.getId());
        Ride ride6 = rideService.requestRide(alice.getId(), alicePickup, aliceDropoff);
        try {
            rideService.rateDriver(ride6.getId(), 4.0);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }
        rideService.acceptRide(ride6.getId());
        rideService.startRide(ride6.getId());
        rideService.completeRide(ride6.getId());
    }
}
