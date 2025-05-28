package star.carsharing.telegram;

import star.carsharing.exception.checked.NotificationException;
import star.carsharing.model.Payment;
import star.carsharing.model.Rental;
import star.carsharing.model.User;

public interface NotificationService {
    void sentNotification(Long userId, String message) throws NotificationException;

    void sentNotificationCreateRental(Rental rental) throws NotificationException;

    void sentNotificationClosedRental(Rental rental) throws NotificationException;

    void sentNotificationOverdueRental(Rental rental) throws NotificationException;

    void sentNotificationNotOverdueRental(Rental rental) throws NotificationException;

    void sentSuccessesPayment(Payment payment) throws NotificationException;

    void sentCancelPayment(Payment payment) throws NotificationException;

    void sentNotificationToManagerNotOverdue(User user) throws NotificationException;

    void sentNotificationToManagerOverdue(User user, Rental rental) throws NotificationException;
}
