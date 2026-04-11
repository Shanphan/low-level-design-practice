package entity;

public enum ReservationStatus {

    PENDING,
    CANCELLED,
    CONFIRMED;

    public boolean canTransitionTo(ReservationStatus next) {

        if (this == ReservationStatus.PENDING) {
            return next == CONFIRMED || next == CANCELLED;
        }
        return false;
    }
}
