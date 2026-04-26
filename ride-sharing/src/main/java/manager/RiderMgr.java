package manager;

import entity.Rider;

import java.util.HashMap;
import java.util.Map;

public class RiderMgr {

    private Map<String, Rider> riders = new HashMap<>();

    public Rider save(Rider rider) {
        riders.put(rider.getId(), rider);
        return rider;
    }

    public Rider findById(String id) {
        return riders.get(id);
    }
}
