package manager;

import entity.Flight;
import entity.Seat;
import enums.SeatStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlightManager {

    private static volatile FlightManager instance;

    private final List<Flight> flights;

    private FlightManager() {
        this.flights = new ArrayList<>();
    }

    public static FlightManager getInstance() {
        if (instance == null) {
            synchronized (FlightManager.class) {
                if (instance == null) {
                    instance = new FlightManager();
                }
            }
        }
        return instance;
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> searchFlights(String sourceCity, String destCity, String date) {
        return flights.stream()
                .filter(f -> f.getSource().getCity().equalsIgnoreCase(sourceCity))
                .filter(f -> f.getDestination().getCity().equalsIgnoreCase(destCity))
                .filter(f -> f.getDepartureTime().startsWith(date))
                .toList();
    }

    public List<Seat> getAvailableSeats(Flight flight) {
        return flight.getSeatAvailability().values().stream()
                .filter(Seat::isAvailable)
                .toList();
    }

    public List<Seat> reserveSeats(Flight flight, List<String> seatIds) {

        Map<String, Seat> flightSeats = flight.getSeatAvailability();

        // resolve actual seat references from the flight's map
        List<Seat> resolvedSeats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = flightSeats.get(seatId);
            if (seat == null) {
                throw new RuntimeException("Seat " + seatId
                        + " does not belong to flight " + flight.getFlightNumber());
            }
            resolvedSeats.add(seat);
        }

        // sort by seat ID to avoid deadlocks (consistent lock ordering)
        resolvedSeats.sort((a, b) -> a.getId().compareTo(b.getId()));

        // lock all seats first
        for (Seat seat : resolvedSeats) {
            seat.getLock().lock();
        }

        try {
            // check all available
            for (Seat seat : resolvedSeats) {
                if (!seat.isAvailable()) {
                    throw new RuntimeException("Seat " + seat.getId() + " is not available");
                }
            }

            // book all
            for (Seat seat : resolvedSeats) {
                seat.setSeatStatus(SeatStatus.BOOKED);
            }

            return resolvedSeats;

        } finally {
            // always unlock all
            for (Seat seat : resolvedSeats) {
                seat.getLock().unlock();
            }
        }
    }

    public void releaseSeats(List<Seat> seats) {

        List<Seat> sortedSeats = new ArrayList<>(seats);
        sortedSeats.sort((a, b) -> a.getId().compareTo(b.getId()));

        for (Seat seat : sortedSeats) {
            seat.getLock().lock();
        }

        try {
            for (Seat seat : sortedSeats) {
                seat.setSeatStatus(SeatStatus.AVAILABLE);
            }
        } finally {
            for (Seat seat : sortedSeats) {
                seat.getLock().unlock();
            }
        }
    }
}
