package exceptions;

public class ReservationNotFound extends RuntimeException {

    String message;
    public ReservationNotFound(String message) {
        super((message));
    }
}
