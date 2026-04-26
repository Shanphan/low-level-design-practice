package entity;

import service.IdGenerator;

public class Ride {

    private String id;
    private String riderId;
    private String driverId;
    private Location pickup;
    private Location dropoff;
    private RideStatus status;
    private double fare;

    public Ride(String riderId, String driverId, Location pickup, Location dropoff) {
        this.id = IdGenerator.createId("RIDE");
        this.riderId = riderId;
        this.driverId = driverId;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.status = RideStatus.REQUESTED;
        this.fare = 0.0;
    }

    public String getId() { return id; }
    public String getRiderId() { return riderId; }
    public String getDriverId() { return driverId; }
    public Location getPickup() { return pickup; }
    public Location getDropoff() { return dropoff; }
    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    @Override
    public String toString() {
        return "Ride[" + id + " rider=" + riderId + " driver=" + driverId
                + " status=" + status + " fare=" + fare
                + " pickup=" + pickup + " dropoff=" + dropoff + "]";
    }
}
