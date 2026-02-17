package manager;

import entity.Booking;
import entity.Flight;
import entity.Passenger;
import entity.Payment;
import entity.Seat;
import enums.BookingStatus;
import enums.PaymentMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingManager {

    private static volatile BookingManager instance;

    private final FlightManager flightManager;
    private final List<Booking> bookings;
    private int bookingCounter;
    private int paymentCounter;

    private BookingManager() {
        this.flightManager = FlightManager.getInstance();
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

    public Booking createBooking(Flight flight, Passenger passenger, List<String> seatIds,
                                 PaymentMode paymentMode) {

        List<Seat> bookedSeats = flightManager.reserveSeats(flight, seatIds);

        int totalPrice = bookedSeats.stream().mapToInt(Seat::getPrice).sum();

        String paymentId = "PAY" + (++paymentCounter);
        Payment payment = new Payment(paymentId, paymentMode, totalPrice);

        String bookingId = "BK" + (++bookingCounter);
        Booking booking = new Booking(bookingId, flight, passenger, bookedSeats, totalPrice, payment);
        booking.setStatus(BookingStatus.CONFIRMED);

        bookings.add(booking);
        return booking;
    }

    public Booking cancelBooking(String bookingId) {

        Optional<Booking> bookingOpt = bookings.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();

        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking " + bookingId + " not found");
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking " + bookingId + " is already cancelled");
        }

        flightManager.releaseSeats(booking.getBookedSeats());
        booking.setStatus(BookingStatus.CANCELLED);

        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookings;
    }
}
