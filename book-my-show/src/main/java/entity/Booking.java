package entity;

import enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class Booking {

    String id;
    Show show;
    List<Seat> bookedSeats;
    int totalPrice;
    BookingStatus status;
    Payment payment;

    public Booking(String id, Show show, List<Seat> bookedSeats, int totalPrice, Payment payment) {
        this.id = id;
        this.show = show;
        this.bookedSeats = bookedSeats;
        this.totalPrice = totalPrice;
        this.payment = payment;
        this.status = BookingStatus.PENDING;
    }
}
