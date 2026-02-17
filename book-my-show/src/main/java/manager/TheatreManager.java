package manager;

import entity.Seat;
import entity.Show;
import entity.Theatre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheatreManager {

    private static volatile TheatreManager instance;

    Map<String, List<Theatre>> theatresByCity;

    private TheatreManager() {
        this.theatresByCity = new HashMap<>();
    }

    public static TheatreManager getInstance() {
        if (instance == null) {
            synchronized (TheatreManager.class) {
                if (instance == null) {
                    instance = new TheatreManager();
                }
            }
        }
        return instance;
    }

    public void addTheatre(Theatre theatre, String city) {
        List<Theatre> theatresInCity = theatresByCity.getOrDefault(city, new ArrayList<>());
        theatresInCity.add(theatre);
        theatresByCity.put(city, theatresInCity);
    }

    public Map<Theatre, List<Show>> getAllShows(String city, String movieName) {


        Map<Theatre, List<Show>> movieShows = new HashMap<>();
        List<Theatre> theatresInCity = theatresByCity.get(city);

        theatresInCity.forEach(theatre -> {
            List<Show> shows = theatre.getShows().stream()
                    .filter(show -> show.getMovie().getName().equalsIgnoreCase(movieName))
                    .toList();

            if(!shows.isEmpty()) {
                movieShows.put(theatre, shows);
            }
        });
        return movieShows;
    }

    public List<Seat> getAvailableSeats(Show show) {

        return show.getSeatAvailability()
                .values().stream()
                .filter(Seat::isAvailable)
                .toList();
    }

    public List<Seat> reserveSeats(Show show, List<Seat> seats) {

        // sort by seat ID to avoid deadlocks (consistent lock ordering)
        List<Seat> sortedSeats = new ArrayList<>(seats);
        sortedSeats.sort((a, b) -> a.getId().compareTo(b.getId()));

        // lock all seats first
        for (Seat seat : sortedSeats) {
            seat.getLock().lock();
        }

        try {
            // validate seats belong to this show
            Map<String, Seat> showSeats = show.getSeatAvailability();
            for (Seat seat : sortedSeats) {
                if (!showSeats.containsKey(seat.getId())) {
                    throw new RuntimeException("Seat " + seat.getId() + " does not belong to show " + show.getShowId());
                }
            }

            // check all available
            for (Seat seat : sortedSeats) {
                if (!seat.isAvailable()) {
                    throw new RuntimeException("Seat " + seat.getId() + " is not available");
                }
            }

            // book all
            for (Seat seat : sortedSeats) {
                seat.setAvailable(false);
            }

            return sortedSeats;

        } finally {
            // always unlock all
            for (Seat seat : sortedSeats) {
                seat.getLock().unlock();
            }
        }
    }
}
