package star.carsharing.service;

import static org.mockito.Mockito.when;
import static star.carsharing.util.AuthTestUtil.userDetails;
import static star.carsharing.util.CarTestUtil.car;
import static star.carsharing.util.PaymentTestUtil.paymentRequestDto;
import static star.carsharing.util.PaymentTestUtil.sessionCreateParams;
import static star.carsharing.util.RentalTestUnit.closedRental;

import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.mapper.PaymentMapper;
import star.carsharing.model.Car;
import star.carsharing.model.Payment;
import star.carsharing.model.Rental;
import star.carsharing.model.User;
import star.carsharing.repository.PaymentRepository;
import star.carsharing.repository.RentalRepository;
import star.carsharing.service.impl.PaymentServiceImpl;
import star.carsharing.telegram.NotificationService;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripePaymentService stripePaymentService;
    @Mock
    private NotificationService notificationService;

    @DisplayName("Verify method createSession with correct data")
    public void createSession_CorrectData_ReturnPaymentResponseDto() {
        PaymentRequestDto paymentRequestDto = paymentRequestDto(Payment.Type.PAYMENT);
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        BigDecimal amount = BigDecimal.valueOf(246.18);
        SessionCreateParams sessionCreateParams = sessionCreateParams(amount);

        when(rentalRepository.findByIdAndUserId(paymentRequestDto.rentalId(), user.getId()))
                .thenReturn(Optional.of(rental));
        when(stripePaymentService.createSessionParams(amount)).thenReturn(sessionCreateParams);

        //PaymentResponseDto actual = paymentService.createSession(user.getId(), paymentRequestDto);
    }
}
