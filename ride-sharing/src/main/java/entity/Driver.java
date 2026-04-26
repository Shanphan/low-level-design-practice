package entity;

import service.IdGenerator;

public class Driver {

    private String id;
    private String name;
    private Location location;
    private DriverStatus status;
    private double rating;
    private int totalRatings;

    public Driver(String name, Location location) {
        this.id = IdGenerator.createId("DRIVER");
        this.name = name;
        this.location = location;
        this.status = DriverStatus.OFFLINE;
        this.rating = 5.0;
        this.totalRatings = 0;
    }

    public void addRating(double score) {
        double total = rating * totalRatings + score;
        totalRatings++;
        rating = total / totalRatings;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }
    public double getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }

    @Override
    public String toString() {
        return "Driver[" + id + " " + name + " status=" + status + " rating=" + String.format("%.1f", rating) + " loc=" + location + "]";
    }
}
