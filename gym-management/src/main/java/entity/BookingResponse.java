package entity;

public class BookingResponse {

    private final String id;
    private final String className;
    private final String userName;
    private final String startTime;
    private final String endTime;
    private final String bookingTime;
    private final String status;

    public BookingResponse(String id, String userName, String className, String startTime, String endTime, String bookingTime, String status) {
        this.id = id;
        this.userName = userName;
        this.className = className;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", user=" + userName + ", class=" + className
                + ", time=" + startTime + "-" + endTime
                + ", booked=" + bookingTime + ", status=" + status + "}";
    }
}
