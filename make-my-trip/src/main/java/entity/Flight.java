package entity;

import enums.FlightStatus;
import enums.SeatStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Flight {

    String id;
    String flightNumber;
    Airplane airplane;
    Airport source;
    Airport destination;
    String departureTime;
    String arrivalTime;
    FlightStatus flightStatus;
    Map<String, Seat> seatAvailability;

    public Flight(String id, String flightNumber, Airplane airplane, Airport source,
                  Airport destination, String departureTime, String arrivalTime) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.airplane = airplane;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightStatus = FlightStatus.SCHEDULED;

        // copy seat layout from airplane (each flight gets its own seat availability)
        this.seatAvailability = new HashMap<>();
        for (Map.Entry<String, Seat> entry : airplane.getSeatLayout().entrySet()) {
            Seat original = entry.getValue();
            Seat copy = new Seat(original.getId(), original.getSeatNumber(),
                    original.getSeatType(), original.getPrice());
            this.seatAvailability.put(entry.getKey(), copy);
        }
    }
}
