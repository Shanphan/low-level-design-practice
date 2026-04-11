package exceptions;

public class ProductStillReservedException extends RuntimeException {
    
    private String message;
    
    public ProductStillReservedException(String message) {
        super(message);
    }
    
}
