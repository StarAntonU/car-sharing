package star.carsharing.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import star.carsharing.exception.checked.NotificationException;
import star.carsharing.exception.checked.RegistrationException;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.exception.unchecked.ForbiddenOperationException;
import star.carsharing.exception.unchecked.InsufficientQuantityException;
import star.carsharing.exception.unchecked.PaymentException;
import star.carsharing.exception.unchecked.SessionFallException;
import star.carsharing.exception.unchecked.TelegramApiException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({NotificationException.class})
    public ResponseEntity<Object> handlerInsufficientQuantityException(
            NotificationException exception) {
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(exception.getMessage());
    }

    @ExceptionHandler({TelegramApiException.class})
    public ResponseEntity<Object> handlerInsufficientQuantityException(
            TelegramApiException exception) {
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(exception.getMessage());
    }

    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<Object> handlerInsufficientQuantityException(
            PaymentException exception) {
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(exception.getMessage());
    }

    @ExceptionHandler({SessionFallException.class})
    public ResponseEntity<Object> handlerInsufficientQuantityException(
            SessionFallException exception) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InsufficientQuantityException.class})
    public ResponseEntity<Object> handlerInsufficientQuantityException(
            InsufficientQuantityException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({ForbiddenOperationException.class})
    public ResponseEntity<Object> handlerForbiddenOperationException(
            ForbiddenOperationException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handlerEntityNotFoundException(
            EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({RegistrationException.class})
    public ResponseEntity<Object> handlerRegistrationException(
            RegistrationException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }
}
