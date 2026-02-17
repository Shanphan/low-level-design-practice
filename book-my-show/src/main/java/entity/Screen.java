package entity;

import enums.ScreenType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Screen {

    private String id;
    private String name;
    private int capacity;
    private Map<String, Seat> seatLayout;
    private ScreenType screenType;


    public Screen(String id, String name, int capacity, ScreenType screenType) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.screenType = screenType;
        this.seatLayout = new HashMap<>();
    }

    public void addSeat(Seat seat) {
        this.seatLayout.put(seat.getId(), seat);
    }

    public void addSeats(List<Seat> seats) {
        seats.forEach(this::addSeat);
    }
}
