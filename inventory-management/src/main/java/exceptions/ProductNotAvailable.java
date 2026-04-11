package exceptions;

public class ProductNotAvailable extends  RuntimeException {

    String message;

    public ProductNotAvailable (String message) {
        super(message);
    }
}
