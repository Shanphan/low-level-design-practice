package entity;

public class Reservation {

    private String id;
    private String productId;
    private String userId;
    private ReservationStatus reservationStatus;
    private Integer quantity;

    public Reservation(String productId, String userId, Integer quantity) {
        this.id = IdGenerator.generate("RESERVATION");
        this.productId = productId;
        this.userId = userId;
        this.reservationStatus = ReservationStatus.PENDING;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {

        return "Reservation [" + this.getId() + ", " + this.getProductId() + ", " + this.getUserId()
                + ", " + this.getReservationStatus().name() + ", " + this.getQuantity() + "]";
    }
}
