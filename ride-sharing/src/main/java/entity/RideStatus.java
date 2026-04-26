package entity;

public enum RideStatus {

    REQUESTED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(RideStatus next) {
        return switch (this) {
            case REQUESTED -> next == ACCEPTED || next == CANCELLED;
            case ACCEPTED -> next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
