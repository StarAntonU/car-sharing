package star.carsharing.exception.unchecked;

public class SessionFallException extends RuntimeException {
    public SessionFallException(String message, Throwable cause) {
        super(message, cause);
    }
}
