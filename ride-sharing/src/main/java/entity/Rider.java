package entity;

import service.IdGenerator;

public class Rider {

    private String id;
    private String name;
    private Location location;
    private double rating;
    private int totalRatings;

    public Rider(String name, Location location) {
        this.id = IdGenerator.createId("RIDER");
        this.name = name;
        this.location = location;
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
    public double getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }

    @Override
    public String toString() {
        return "Rider[" + id + " " + name + " rating=" + String.format("%.1f", rating) + "]";
    }
}
