import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingSpotManager {
    private Map<SpotType, List<ParkingSpot>> parkingSpots;
    private static ParkingSpotManager instance;
    private ParkingSpotManager() {
        this.parkingSpots = new HashMap<>();
    }

    public static ParkingSpotManager getInstance() {

        if(instance == null) {
            return new ParkingSpotManager();
        }

        return instance;
    }

    public Map<SpotType, List<ParkingSpot>> getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(Map<SpotType, List<ParkingSpot>> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    public void createParkingSpots (int numTwoWheeler, int fourWheeler) {



        List<ParkingSpot> parkingSpots1 = new ArrayList<>(numTwoWheeler);
        List<ParkingSpot> parkingSpots2 = new ArrayList<>(fourWheeler);

        for(int i = 0; i < numTwoWheeler; i++) {
            parkingSpots1.add(new TwoWheelerSpot());
        }

        for(int i = 0; i < fourWheeler; i++) {
            parkingSpots2.add(new FourWheelerSpot());
        }

        parkingSpots.put(SpotType.TWO_WHEELER, parkingSpots1);
        parkingSpots.put(SpotType.FOUR_WHEELER, parkingSpots2);

    }

    public void addNewParkingSpot(SpotType parkingSpotType) {
        if(parkingSpotType == SpotType.TWO_WHEELER) {
            parkingSpots.get(SpotType.TWO_WHEELER).add(new TwoWheelerSpot());
        } else if(parkingSpotType == SpotType.FOUR_WHEELER) {
            parkingSpots.get(SpotType.FOUR_WHEELER).add(new FourWheelerSpot());
        }
    }
}
