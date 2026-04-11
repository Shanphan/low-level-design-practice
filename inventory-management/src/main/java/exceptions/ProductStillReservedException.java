package exceptions;

public class ProductStillReservedException extends RuntimeException {
    public ProductStillReservedException(String message) {
        super(message);
    }
    
}
