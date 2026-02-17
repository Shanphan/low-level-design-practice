package entity;

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
    int rowNumber;
    int seatNumber;
    boolean isAvailable;
    SeatType seatType;
    int price;
    private final ReentrantLock lock = new ReentrantLock();

    public Seat(String id, int rowNumber, int seatNumber, SeatType seatType, int price) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.isAvailable = true;
        this.seatType = seatType;
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
