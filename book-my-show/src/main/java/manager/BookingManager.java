package manager;

import entity.Booking;
import entity.Payment;
import entity.Seat;
import entity.Show;
import enums.PaymentMode;

import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private static volatile BookingManager instance;

    private final TheatreManager theatreManager;
    private final List<Booking> bookings;
    private int bookingCounter;
    private int paymentCounter;

    private BookingManager() {
        this.theatreManager = TheatreManager.getInstance();
        this.bookings = new ArrayList<>();
        this.bookingCounter = 0;
        this.paymentCounter = 0;
    }

    public static BookingManager getInstance() {
        if (instance == null) {
            synchronized (BookingManager.class) {
                if (instance == null) {
                    instance = new BookingManager();
                }
            }
        }
        return instance;
    }

    public Booking createBooking(Show show, List<Seat> seats, PaymentMode paymentMode) {

        List<Seat> bookedSeats = theatreManager.reserveSeats(show, seats);

        int totalPrice = bookedSeats.stream().mapToInt(Seat::getPrice).sum();

        String paymentId = "PAY" + (++paymentCounter);
        Payment payment = new Payment(paymentId, paymentMode, totalPrice);

        String bookingId = "BK" + (++bookingCounter);
        Booking booking = new Booking(bookingId, show, bookedSeats, totalPrice, payment);

        bookings.add(booking);
        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookings;
    }
}
