package star.carsharing.dto.payment;

import java.math.BigDecimal;
import star.carsharing.model.Payment;

public record PaymentDto(
        Long id,
        Payment.Status status,
        Payment.Type type,
        Long rentalId,
        BigDecimal amount
) {
}
