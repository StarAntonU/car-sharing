package star.carsharing.exception.unchecked;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
