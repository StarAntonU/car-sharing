package star.carsharing.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import star.carsharing.model.Payment;

public record PaymentRequestDto(
        @NotNull
        @PositiveOrZero
        Long rentalId,
        @NotNull
        Payment.Type paymentType
) {
}
