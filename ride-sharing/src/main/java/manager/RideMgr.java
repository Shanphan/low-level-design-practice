package manager;

import entity.Ride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideMgr {

    private Map<String, Ride> rides = new HashMap<>();

    public Ride save(Ride ride) {
        rides.put(ride.getId(), ride);
        return ride;
    }

    public Ride findById(String id) {
        return rides.get(id);
    }

    public List<Ride> findByRiderId(String riderId) {
        return rides.values().stream()
                .filter(r -> r.getRiderId().equals(riderId))
                .toList();
    }
}
