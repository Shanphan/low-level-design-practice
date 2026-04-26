package service;

import entity.Rider;
import exception.RiderNotFoundException;
import manager.RiderMgr;

public class RiderService {

    private final RiderMgr riderMgr;

    public RiderService(RiderMgr riderMgr) {
        this.riderMgr = riderMgr;
    }

    public Rider register(Rider rider) {
        return riderMgr.save(rider);
    }

    public Rider getRider(String id) {
        Rider rider = riderMgr.findById(id);
        if (rider == null) throw new RiderNotFoundException("Rider not found: " + id);
        return rider;
    }
}
