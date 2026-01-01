public class ParkingSpot extends Entity {

    boolean isEmpty;
    Vehicle vehicle;
    int price;
    SpotType parkingSpotType;

    public ParkingSpot(int price, SpotType parkingSpotType) {
        this.price = price;
        this.parkingSpotType = parkingSpotType;
        this.isEmpty = true;
    }

    public void parkVehicle (Vehicle vehicle) {
        this.vehicle = vehicle;
        isEmpty = false;
    }
    public void removeVehicle () {
        this.vehicle = null;
        isEmpty = true;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
