package manager;

import entity.Reservation;
import entity.ReservationStatus;

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

    public Reservation findReservationByProductIdAndUserId(String userId, String productId) {

        return reservations.values()
                .stream()
                .filter(r -> r.getUserId().equals(userId) )
                .filter(r -> r.getProductId().equals(productId) )
                .filter(r -> r.getReservationStatus().equals(ReservationStatus.PENDING))
                .findFirst()
                .orElse(null);
    }
}
