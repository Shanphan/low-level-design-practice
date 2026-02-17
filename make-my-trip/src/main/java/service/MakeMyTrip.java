package service;

import entity.*;
import enums.PaymentMode;
import enums.SeatType;
import manager.AirportManager;
import manager.BookingManager;
import manager.FlightManager;

import java.util.List;

public class MakeMyTrip {

    private final AirportManager airportManager;
    private final FlightManager flightManager;
    private final BookingManager bookingManager;

    public MakeMyTrip() {
        this.airportManager = AirportManager.getInstance();
        this.flightManager = FlightManager.getInstance();
        this.bookingManager = BookingManager.getInstance();
    }

    public void initialize() {

        // 1. Create airports
        Airport blr = new Airport("AP1", "Kempegowda International Airport", "BLR", "Bangalore");
        Airport del = new Airport("AP2", "Indira Gandhi International Airport", "DEL", "Delhi");
        Airport bom = new Airport("AP3", "Chhatrapati Shivaji International Airport", "BOM", "Mumbai");

        airportManager.addAirport(blr);
        airportManager.addAirport(del);
        airportManager.addAirport(bom);

        // 2. Create Airplane 1 (Boeing 737) with seat layout
        Airplane airplane1 = new Airplane("AR1", "Boeing 737", "IndiGo", 6);
        airplane1.addSeats(List.of(
                new Seat("S1", "1A", SeatType.BUSINESS, 8000),
                new Seat("S2", "1B", SeatType.BUSINESS, 8000),
                new Seat("S3", "10A", SeatType.PREMIUM_ECONOMY, 5000),
                new Seat("S4", "10B", SeatType.PREMIUM_ECONOMY, 5000),
                new Seat("S5", "20A", SeatType.ECONOMY, 3000),
                new Seat("S6", "20B", SeatType.ECONOMY, 3000)
        ));

        // 3. Create Airplane 2 (Airbus A320) with seat layout
        Airplane airplane2 = new Airplane("AR2", "Airbus A320", "Air India", 4);
        airplane2.addSeats(List.of(
                new Seat("S7", "1A", SeatType.FIRST_CLASS, 15000),
                new Seat("S8", "5A", SeatType.BUSINESS, 9000),
                new Seat("S9", "15A", SeatType.ECONOMY, 4000),
                new Seat("S10", "15B", SeatType.ECONOMY, 4000)
        ));

        // 4. Create Airplane 3 (Boeing 787) with seat layout
        Airplane airplane3 = new Airplane("AR3", "Boeing 787", "Vistara", 3);
        airplane3.addSeats(List.of(
                new Seat("S11", "1A", SeatType.BUSINESS, 7500),
                new Seat("S12", "12A", SeatType.ECONOMY, 2800),
                new Seat("S13", "12B", SeatType.ECONOMY, 2800)
        ));

        // 5. Create flights (airplane + route + schedule)
        // Each flight copies the seat layout from its airplane
        Flight flight1 = new Flight("F1", "MMT-101", airplane1, blr, del,
                "2024-06-01 06:00", "2024-06-01 08:30");

        Flight flight2 = new Flight("F2", "MMT-202", airplane2, del, bom,
                "2024-06-01 10:00", "2024-06-01 12:00");

        Flight flight3 = new Flight("F3", "MMT-103", airplane3, blr, del,
                "2024-06-01 14:00", "2024-06-01 16:30");

        // 6. Register flights
        flightManager.addFlight(flight1);
        flightManager.addFlight(flight2);
        flightManager.addFlight(flight3);
    }

    // ==================== BOOKING FLOW ====================

    // Step 1: Search flights by source city, destination city, and date
    public List<Flight> searchFlights(String sourceCity, String destCity, String date) {
        return flightManager.searchFlights(sourceCity, destCity, date);
    }

    // Step 2: View available seats on a flight
    public List<Seat> getAvailableSeats(Flight flight) {
        return flightManager.getAvailableSeats(flight);
    }

    // Step 3: Book a flight (pass seat IDs, not seat objects — manager resolves from flight)
    public Booking bookFlight(Flight flight, Passenger passenger, List<String> seatIds,
                              PaymentMode paymentMode) {
        return bookingManager.createBooking(flight, passenger, seatIds, paymentMode);
    }

    // Step 4: Cancel a booking
    public Booking cancelBooking(String bookingId) {
        return bookingManager.cancelBooking(bookingId);
    }

    // ==================== SIMULATE FULL FLOW ====================

    public void simulateBookingFlow() {

        String sourceCity = "Bangalore";
        String destCity = "Delhi";
        String date = "2024-06-01";

        // Step 1: Search flights
        List<Flight> flights = searchFlights(sourceCity, destCity, date);
        System.out.println("Step 1 - Flights from " + sourceCity + " to " + destCity + " on " + date + ":");
        flights.forEach(f -> System.out.println("  " + f.getFlightNumber()
                + " | " + f.getAirplane().getAirline() + " (" + f.getAirplane().getModel() + ")"
                + " | " + f.getSource().getCode() + " -> " + f.getDestination().getCode()
                + " | Departure: " + f.getDepartureTime()
                + " | Arrival: " + f.getArrivalTime()));

        // Step 2: Select a flight and view available seats
        Flight selectedFlight = flights.get(0);
        List<Seat> availableSeats = getAvailableSeats(selectedFlight);
        System.out.println("\nStep 2 - Available seats on flight " + selectedFlight.getFlightNumber()
                + " (" + selectedFlight.getAirplane().getModel() + "):");
        availableSeats.forEach(s -> System.out.println("  " + s.getSeatNumber()
                + " | Type: " + s.getSeatType()
                + " | Price: " + s.getPrice()));

        // Step 3: Create passenger and book seats (pass IDs, not objects)
        Passenger passenger = new Passenger("P1", "Rahul Sharma", 28, "rahul@email.com");
        List<String> seatIdsToBook = availableSeats.subList(0, 2).stream()
                .map(Seat::getId).toList();
        System.out.println("\nStep 3 - Booking seat IDs: " + seatIdsToBook);

        Booking booking = bookFlight(selectedFlight, passenger, seatIdsToBook, PaymentMode.UPI);
        System.out.println("\nBooking created!");
        System.out.println("  Booking ID: " + booking.getId());
        System.out.println("  Passenger: " + booking.getPassenger().getName());
        System.out.println("  Flight: " + booking.getFlight().getFlightNumber());
        System.out.println("  Airplane: " + booking.getFlight().getAirplane().getModel());
        System.out.println("  Total Price: " + booking.getTotalPrice());
        System.out.println("  Status: " + booking.getStatus());
        System.out.println("  Seats: " + booking.getBookedSeats().stream()
                .map(Seat::getSeatNumber).toList());

        // Verify seats are now taken
        List<Seat> remainingSeats = getAvailableSeats(selectedFlight);
        System.out.println("\n--- Remaining available seats: " + remainingSeats.size());

        // Step 4: Cancel booking
        System.out.println("\nStep 4 - Cancelling booking " + booking.getId() + "...");
        Booking cancelledBooking = cancelBooking(booking.getId());
        System.out.println("  Booking Status: " + cancelledBooking.getStatus());

        // Verify seats are released
        List<Seat> seatsAfterCancel = getAvailableSeats(selectedFlight);
        System.out.println("  Available seats after cancellation: " + seatsAfterCancel.size());
    }

    public static void main(String[] args) {
        MakeMyTrip app = new MakeMyTrip();
        app.initialize();
        app.simulateBookingFlow();
    }
}
