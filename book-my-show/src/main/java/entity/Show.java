package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Show {

    String showId;
    Movie movie;
    Screen screen;
    String showTime;
    Map<String, Seat> seatAvailability;

    public Show(String showId, Movie movie, Screen screen, String showTime) {
        this.showId = showId;
        this.movie = movie;
        this.screen = screen;
        this.showTime = showTime;
        this.seatAvailability = new HashMap<>();
        for (Map.Entry<String, Seat> entry : screen.getSeatLayout().entrySet()) {
            Seat original = entry.getValue();
            Seat copy = new Seat(original.getId(), original.getRowNumber(),
                    original.getSeatNumber(), original.getSeatType(), original.getPrice());
            this.seatAvailability.put(entry.getKey(), copy);
        }
    }
}
