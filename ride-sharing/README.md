# Ride Sharing / Cab Booking — LLD Machine Coding

Uber/Ola-style ride matching and booking system. Built for Meesho SDE-3 interview practice.

## Requirements

1. Register riders and drivers
2. Drivers go online/offline
3. Rider requests ride — system matches to nearest available driver
4. Full ride lifecycle: REQUESTED → ACCEPTED → IN_PROGRESS → COMPLETED
5. Cancel ride (from REQUESTED or ACCEPTED)
6. Calculate fare (base + distance, support surge pricing)
7. Rate driver and rider after ride completion
8. Ride history for riders
9. Swappable matching strategy (nearest, highest rated)
10. Swappable pricing strategy (base, surge)

## Architecture

```
Main.java (driver)
    |
    v
Service Layer
    RiderService     — register, getRider
    DriverService    — register, goOnline, goOffline, updateLocation
    RideService      — requestRide, accept, start, complete, cancel, rate
    |
    v
Strategy Layer
    MatchingStrategy  — NearestDriverStrategy, HighestRatedDriverStrategy
    PricingStrategy   — BasePricingStrategy, SurgePricingStrategy
    |
    v
Manager Layer
    RiderMgr    — Map<String, Rider>
    DriverMgr   — Map<String, Driver>, findAvailable()
    RideMgr     — Map<String, Ride>, findByRiderId()
    |
    v
Entity Layer
    Rider       — id, name, location, rating, totalRatings
    Driver      — id, name, location, status, rating, totalRatings
    Ride        — id, riderId, driverId, pickup, dropoff, status, fare
    Location    — lat, lng, distanceTo()
    RideStatus  — REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    DriverStatus — AVAILABLE, ON_TRIP, OFFLINE
```

## Key Design Decisions

### 1. Strategy Pattern — Matching

```java
public interface MatchingStrategy {
    Driver match(Location pickup, List<Driver> availableDrivers);
}
```

- **NearestDriverStrategy** — min distance from pickup
- **HighestRatedDriverStrategy** — max driver rating

Injected into RideService at construction. Swap at runtime (different time of day, different strategy).

### 2. Strategy Pattern — Pricing

```java
public interface PricingStrategy {
    double calculateFare(Location pickup, Location dropoff);
}
```

- **BasePricingStrategy** — BASE_FARE + distance x PER_UNIT
- **SurgePricingStrategy** — wraps base pricing x surge multiplier

Fare calculated on ride completion, not on request. Real-world: estimate at request, finalize at completion.

### 3. State Machine on RideStatus

```
REQUESTED → ACCEPTED → IN_PROGRESS → COMPLETED
     ↘          ↘
   CANCELLED  CANCELLED
```

`canTransitionTo()` on the enum enforces valid transitions. Same pattern as ReservationStatus in inventory management.

### 4. Driver Status Lifecycle

```
OFFLINE ↔ AVAILABLE → ON_TRIP → AVAILABLE
```

- `goOnline()` — OFFLINE → AVAILABLE
- `requestRide()` — AVAILABLE → ON_TRIP (assigned)
- `completeRide()` / `cancelRide()` — ON_TRIP → AVAILABLE
- `goOffline()` — AVAILABLE → OFFLINE (blocked if ON_TRIP)

### 5. Rating System

```java
public void addRating(double score) {
    double total = rating * totalRatings + score;
    totalRatings++;
    rating = total / totalRatings;
}
```

Running average on both Rider and Driver. Can only rate after COMPLETED. Used by HighestRatedDriverStrategy for matching.

### 6. Location Distance

```java
public double distanceTo(Location other) {
    double dlat = this.lat - other.lat;
    double dlng = this.lng - other.lng;
    return Math.sqrt(dlat * dlat + dlng * dlng);
}
```

Euclidean distance for simplicity. Mention Haversine formula for real lat/lng in interview.

## Core Flows

```
requestRide(riderId, pickup, dropoff)
    1. Validate rider exists
    2. Get all AVAILABLE drivers
    3. Apply matching strategy → pick best driver
    4. Create Ride(REQUESTED)
    5. Driver status → ON_TRIP
    6. Return ride

completeRide(rideId)
    1. Validate ride exists
    2. Validate state transition (IN_PROGRESS → COMPLETED)
    3. Calculate fare using pricing strategy
    4. Driver status → AVAILABLE
    5. Return ride

cancelRide(rideId)
    1. Validate ride exists
    2. Validate state transition (REQUESTED/ACCEPTED → CANCELLED)
    3. Driver status → AVAILABLE
```

## Edge Cases Handled

| Edge Case | Where | Behavior |
|-----------|-------|----------|
| No drivers available | RideService.requestRide | NoDriverAvailableException |
| All drivers offline | RideService.requestRide | NoDriverAvailableException |
| Invalid state transition | RideService (all transitions) | InvalidRideStateException |
| Complete a cancelled ride | RideService.completeRide | InvalidRideStateException |
| Go offline while on trip | DriverService.goOffline | IllegalStateException |
| Rate before completion | RideService.rateDriver | InvalidRideStateException |
| Rating out of range | RideService.rateDriver | IllegalArgumentException |
| Rider not found | RideService.requestRide | RiderNotFoundException |
| Driver not found | DriverService.getDriver | DriverNotFoundException |

## Demo Scenarios (Main.java)

1. Alice requests ride — nearest driver matched
2. Full lifecycle: accept → start → complete
3. Rate driver and rider
4. Bob requests ride (fewer drivers after #1)
5. Cancel ride — driver becomes available
6. Surge pricing (2x multiplier)
7. Highest rated driver matching
8. Ride history for Alice
9. No drivers available (all offline)
10. Invalid state transition (complete cancelled ride)
11. Cannot go offline while on trip
12. Rate before completion (throws)

## Scaling Discussion (Verbal)

### Driver Matching at Scale
Linear scan of all drivers won't work with 100K+ drivers. Solutions:
- **Geospatial index** — QuadTree, R-Tree, or geohash grid
- **Redis GEO** — `GEORADIUS` for nearby drivers within radius
- **Pre-bucketed zones** — partition city into zones, only search within rider's zone + adjacent

### Surge Pricing
- Track demand (ride requests) vs supply (available drivers) per zone
- High demand/low supply → surge multiplier increases
- Real-time calculation, updated every few minutes

### Concurrency
- Multiple riders requesting same driver simultaneously
- Solution: lock per driver (like product lock in inventory), or optimistic retry
- Driver assignment must be atomic — check AVAILABLE + set ON_TRIP in one operation

## Project Structure

```
src/main/java/
    Main.java
    entity/
        Rider.java
        Driver.java
        Ride.java
        Location.java
        RideStatus.java
        DriverStatus.java
    manager/
        RiderMgr.java
        DriverMgr.java
        RideMgr.java
    service/
        RiderService.java
        DriverService.java
        RideService.java
        IdGenerator.java
    strategy/
        MatchingStrategy.java
        NearestDriverStrategy.java
        HighestRatedDriverStrategy.java
        PricingStrategy.java
        BasePricingStrategy.java
        SurgePricingStrategy.java
    exception/
        RiderNotFoundException.java
        DriverNotFoundException.java
        RideNotFoundException.java
        NoDriverAvailableException.java
        InvalidRideStateException.java
```
