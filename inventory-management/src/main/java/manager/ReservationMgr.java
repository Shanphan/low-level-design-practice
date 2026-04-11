package manager;

import entity.Reservation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationMgr {

    private final Map<String, Reservation>  reservations;

    public ReservationMgr() {
        this.reservations = new ConcurrentHashMap<>();
    }

    public Reservation save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    public Reservation findById (String id) {
        return reservations.get(id);
    }
}
