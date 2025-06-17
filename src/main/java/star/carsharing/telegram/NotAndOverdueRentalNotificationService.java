package star.carsharing.telegram;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import star.carsharing.exception.checked.NotificationException;
import star.carsharing.exception.unchecked.TelegramApiException;
import star.carsharing.model.Rental;
import star.carsharing.model.Role;
import star.carsharing.model.User;
import star.carsharing.repository.RentalRepository;
import star.carsharing.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotAndOverdueRentalNotificationService {
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void notificationNotOverdueRents() {
        List<Rental> rentals = rentalRepository.findAllByReturnDateLessThan(LocalDate.now());
        for (Rental rental : rentals) {
            try {
                notificationService.sentNotificationNotOverdueRental(rental);
            } catch (NotificationException e) {
                throw new TelegramApiException("Can`t sent the notification");
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void notificationOverdueRents() {
        List<Rental> rentals =
                rentalRepository.findAllByReturnDateGreaterThanEqual(LocalDate.now());
        List<User> users = userRepository.findAllByRoles(Role.RoleName.MANAGER);
        if (rentals.isEmpty()) {
            for (User user : users) {
                try {
                    notificationService.sentNotificationToManagerNotOverdue(user);
                } catch (NotificationException e) {
                    throw new TelegramApiException("Can`t sent the notification");
                }
            }
            return;
        }
        for (Rental rental : rentals) {
            for (User user : users) {
                try {
                    notificationService.sentNotificationToManagerOverdue(user, rental);
                } catch (NotificationException e) {
                    throw new TelegramApiException("Can`t sent the notification");
                }
            }
            try {
                notificationService.sentNotificationOverdueRental(rental);
            } catch (NotificationException e) {
                throw new TelegramApiException("Can`t sent the notification");
            }
        }
    }
}
