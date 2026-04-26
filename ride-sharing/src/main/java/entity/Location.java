package entity;

public class Location {

    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() { return lat; }
    public double getLng() { return lng; }

    public void setLat(double lat) { this.lat = lat; }
    public void setLng(double lng) { this.lng = lng; }

    public double distanceTo(Location other) {
        double dlat = this.lat - other.lat;
        double dlng = this.lng - other.lng;
        return Math.sqrt(dlat * dlat + dlng * dlng);
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lng + ")";
    }
}
