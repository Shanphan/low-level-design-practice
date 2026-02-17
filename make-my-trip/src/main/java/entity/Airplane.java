package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Airplane {

    String id;
    String model;
    String airline;
    int capacity;
    Map<String, Seat> seatLayout;

    public Airplane(String id, String model, String airline, int capacity) {
        this.id = id;
        this.model = model;
        this.airline = airline;
        this.capacity = capacity;
        this.seatLayout = new HashMap<>();
    }

    public void addSeat(Seat seat) {
        this.seatLayout.put(seat.getId(), seat);
    }

    public void addSeats(List<Seat> seats) {
        seats.forEach(this::addSeat);
    }
}
