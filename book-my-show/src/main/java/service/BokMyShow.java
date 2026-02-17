package service;

import entity.*;
import enums.PaymentMode;
import enums.ScreenType;
import enums.SeatType;
import manager.BookingManager;
import manager.MovieManager;
import manager.TheatreManager;

import java.util.List;
import java.util.Map;

public class BokMyShow {

    private final MovieManager movieManager;
    private final TheatreManager theatreManager;
    private final BookingManager bookingManager;

    public BokMyShow() {
        this.movieManager = MovieManager.getInstance();
        this.theatreManager = TheatreManager.getInstance();
        this.bookingManager = BookingManager.getInstance();
    }

    public void initialize() {

        String city = "Bangalore";

        // 1. Create movies
        Movie movie1 = new Movie("M1", "Dhurandar", 120, "Action");
        Movie movie2 = new Movie("M2", "Pushpa 2", 150, "Action");

        // 2. Create seats
        Seat seat1 = new Seat("SE1", 1, 1, SeatType.REGULAR, 100);
        Seat seat2 = new Seat("SE2", 1, 2, SeatType.REGULAR, 100);
        Seat seat3 = new Seat("SE3", 1, 3, SeatType.PREMIUM, 200);
        Seat seat4 = new Seat("SE4", 2, 1, SeatType.PREMIUM, 200);
        Seat seat5 = new Seat("SE5", 2, 2, SeatType.VIP, 500);

        // 3. Create screens and attach seats
        Screen screen1 = new Screen("SC1", "Screen 1", 3, ScreenType.TWO_D);
        screen1.addSeats(List.of(seat1, seat2, seat3));

        Screen screen2 = new Screen("SC2", "Screen 2", 2, ScreenType.IMAX);
        screen2.addSeats(List.of(seat4, seat5));

        // 4. Create shows (movie + screen + time)
        Show show1 = new Show("SH1", movie1, screen1, "2024-06-01 18:00");
        Show show2 = new Show("SH2", movie2, screen2, "2024-06-01 21:00");

        // 5. Create theatre and attach screens + shows
        Theatre theatre1 = new Theatre("T1", city, "Koramangala, Bangalore");
        theatre1.addScreens(List.of(screen1, screen2));
        theatre1.setShows(List.of(show1, show2));

        // 6. Register with managers
        movieManager.addMovie(movie1, city);
        movieManager.addMovie(movie2, city);
        theatreManager.addTheatre(theatre1, city);

    }

    // ==================== BOOKING FLOW ====================

    // Step 1: Search movies in a city
    public List<Movie> searchMovies(String city) {
        return movieManager.getMoviesByCity(city);
    }

    // Step 2: Select a show (get all shows for a movie in a city)
    public Map<Theatre, List<Show>> getShowsForMovie(String city, String movieName) {
        return theatreManager.getAllShows(city, movieName);
    }

    // Step 3: Select seats (view available seats for a show)
    public List<Seat> getAvailableSeats(Show show) {
        return theatreManager.getAvailableSeats(show);
    }

    // Step 4 + 5: Book seats and create booking
    public Booking bookSeats(Show show, List<Seat> selectedSeats, PaymentMode paymentMode) {
        return bookingManager.createBooking(show, selectedSeats, paymentMode);
    }

    // ==================== SIMULATE FULL FLOW ====================

    public void simulateBookingFlow() {

        String city = "Bangalore";
        String movieName = "Dhurandar";

        // Step 1: Search movies
        List<Movie> movies = searchMovies(city);
        System.out.println("Step 1 - Movies in " + city + ":");
        movies.forEach(m -> System.out.println("  " + m.getId() + " | " + m.getName()));

        // Step 2: Select show
        Map<Theatre, List<Show>> showsByTheatre = getShowsForMovie(city, movieName);
        System.out.println("\nStep 2 - Shows for '" + movieName + "':");
        Show selectedShow = null;
        for (var entry : showsByTheatre.entrySet()) {
            for (Show show : entry.getValue()) {
                System.out.println("  Theatre: " + entry.getKey().getCity()
                        + " | Show: " + show.getShowId()
                        + " | Time: " + show.getShowTime()
                        + " | Screen: " + show.getScreen().getName());
                selectedShow = show;
            }
        }

        // Step 3: Select seats
        List<Seat> availableSeats = getAvailableSeats(selectedShow);
        System.out.println("\nStep 3 - Available seats for show " + selectedShow.getShowId() + ":");
        availableSeats.forEach(s -> System.out.println("  " + s.getId()
                + " | Row: " + s.getRowNumber()
                + " | Seat: " + s.getSeatNumber()
                + " | Type: " + s.getSeatType()
                + " | Price: " + s.getPrice()));

        List<Seat> seatsToBook = availableSeats.subList(0, 2);
        System.out.println("\nStep 4 - Booking seats: "
                + seatsToBook.stream().map(Seat::getId).toList());

        // Step 5: Create booking
        Booking booking = bookSeats(selectedShow, seatsToBook, PaymentMode.UPI);
        System.out.println("\nStep 5 - Booking created!");
        System.out.println("  Booking ID: " + booking.getId());
        System.out.println("  Total Price: " + booking.getTotalPrice());
        System.out.println("  Status: " + booking.getStatus());
        System.out.println("  Seats: " + booking.getBookedSeats().stream().map(Seat::getId).toList());

        // Verify seats are now taken
        List<Seat> remainingSeats = getAvailableSeats(selectedShow);
        System.out.println("\n--- Remaining available seats: " + remainingSeats.size());
    }

    public static void main(String[] args) {
        BokMyShow app = new BokMyShow();
        app.initialize();
        app.simulateBookingFlow();
    }
}
