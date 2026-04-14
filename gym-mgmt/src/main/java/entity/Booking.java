package entity;

import java.time.LocalDateTime;

public class Booking {

    private String id;
    private String customerId;
    private String gymClassId;
    private LocalDateTime bookingTime;
    private BookingStatus status;

    public Booking(String customerId, String gymClassId, LocalDateTime bookingTime) {
        this.id = EntityIdGenerator.getId("BOOKING-");
        this.customerId = customerId;
        this.gymClassId = gymClassId;
        this.bookingTime = bookingTime;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGymClassId() {
        return gymClassId;
    }

    public void setGymClassId(String gymClassId) {
        this.gymClassId = gymClassId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
