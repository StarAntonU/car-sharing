package star.carsharing.telegram;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import star.carsharing.exception.checked.NotificationException;
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
    public void notificationNotOverdueRents() throws NotificationException {
        List<Rental> rentals = rentalRepository.findAllByReturnDateLessThan(LocalDate.now());
        for (Rental rental : rentals) {
            notificationService.sentNotificationNotOverdueRental(rental);
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void notificationOverdueRents() throws NotificationException {
        List<Rental> rentals =
                rentalRepository.findAllByReturnDateGreaterThanEqual(LocalDate.now());
        List<User> users = userRepository.findAllByRoles(Role.RoleName.MANAGER);
        if (rentals.isEmpty()) {
            for (User user : users) {
                notificationService.sentNotificationToManagerNotOverdue(user);
            }
            return;
        }
        for (Rental rental : rentals) {
            for (User user : users) {
                notificationService.sentNotificationToManagerOverdue(user, rental);
            }
            notificationService.sentNotificationOverdueRental(rental);
        }
    }
}
