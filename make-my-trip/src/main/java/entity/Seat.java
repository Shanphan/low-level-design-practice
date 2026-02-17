package entity;

import enums.SeatStatus;
import enums.SeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@NoArgsConstructor
public class Seat {

    String id;
    String seatNumber;
    SeatType seatType;
    int price;
    SeatStatus seatStatus;
    private final ReentrantLock lock = new ReentrantLock();

    public Seat(String id, String seatNumber, SeatType seatType, int price) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.price = price;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return seatStatus == SeatStatus.AVAILABLE;
    }
}
