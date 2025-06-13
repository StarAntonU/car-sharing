package star.carsharing.telegram;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import star.carsharing.exception.checked.NotificationException;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.model.Payment;
import star.carsharing.model.Rental;
import star.carsharing.model.User;
import star.carsharing.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void sentNotification(Long userId, String message) throws NotificationException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user by id " + userId));
        String chatId = user.getTelegramChatId();
        if (chatId == null) {
            throw new NotificationException("Chat id can`t by null");
        }
        String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                TELEGRAM_API_URL, botToken, chatId, message);
        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new NotificationException("Can`t to send a notification", e);
        }
    }

    @Override
    public void sentNotificationCreateRental(Rental rental) throws NotificationException {
        String message = String.format("""
                        Congratulations!\n 
                        You have rented a car %s %s.\n 
                        Return date %s
                        """,
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getReturnDate()
        );
        sentNotification(rental.getUser().getId(), message);
    }

    @Override
    public void sentNotificationClosedRental(Rental rental) throws NotificationException {
        String message = String.format("""
                        You have returned the car %s %s.\n
                        We look forward to seeing you again.
                        """,
                rental.getCar().getBrand(),
                rental.getCar().getModel()
        );
        sentNotification(rental.getUser().getId(), message);
    }

    @Override
    public void sentNotificationOverdueRental(Rental rental) throws NotificationException {
        String message = String.format("""
                        You were supposed to return the car %s %s on the %s!\n
                        You will have to pay a fine!!!
                        """,
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getReturnDate()
        );
        sentNotification(rental.getUser().getId(), message);
    }

    @Override
    public void sentNotificationNotOverdueRental(Rental rental) throws NotificationException {
        String message = String.format("""
                        Hello!\n
                        We are glad that you are using our cars.\n
                        Just a reminder that your rental period ends at %s
                        """,
                rental.getReturnDate()
        );
        sentNotification(rental.getUser().getId(), message);
    }

    @Override
    public void sentSuccessesPayment(Payment payment) throws NotificationException {
        String message = "Your payment was successful!";
        sentNotification(payment.getRental().getUser().getId(), message);
    }

    @Override
    public void sentCancelPayment(Payment payment) throws NotificationException {
        String message = "Your payment was cancelled!";
        sentNotification(payment.getRental().getUser().getId(), message);
    }

    @Override
    public void sentNotificationToManagerNotOverdue(User user) throws NotificationException {
        String message = "No overdue rentals!";
        sentNotification(user.getId(), message);
    }

    @Override
    public void sentNotificationToManagerOverdue(User user, Rental rental)
            throws NotificationException {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now());
        String message = String.format("""
                Rental is overdue %s days
                Car - %s %s
                Costumer - %s %s %s
                """,
                days,
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getUser().getEmail(),
                rental.getUser().getFirstName(),
                rental.getUser().getLastName()
        );
        sentNotification(user.getId(), message);
    }
}

