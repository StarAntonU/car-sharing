package star.carsharing.exception.unchecked;

public class TelegramApiException extends RuntimeException {
    public TelegramApiException(String message) {
        super(message);
    }

    public TelegramApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
