package star.carsharing.telegram;

import star.carsharing.model.Payment;
import star.carsharing.model.Rental;
import star.carsharing.model.User;

public interface NotificationService {
    void sentNotification(Long userId, String message);

    void sentNotificationCreateRental(Rental rental);

    void sentNotificationClosedRental(Rental rental);

    void sentNotificationOverdueRental(Rental rental);

    void sentNotificationNotOverdueRental(Rental rental);

    void sentSuccessesPayment(Payment payment);

    void sentCancelPayment(Payment payment);

    void sentNotificationToManagerNotOverdue(User user);

    void sentNotificationToManagerOverdue(User user, Rental rental);
}
