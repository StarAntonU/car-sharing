package star.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static star.carsharing.util.AuthTestUtil.userDetails;
import static star.carsharing.util.CarTestUtil.car;
import static star.carsharing.util.PaymentTestUtil.mapPaymentRequestDtoToPayment;
import static star.carsharing.util.PaymentTestUtil.mapPaymentToPaymentDto;
import static star.carsharing.util.PaymentTestUtil.mapPaymentToPaymentResponseDto;
import static star.carsharing.util.PaymentTestUtil.mockSession;
import static star.carsharing.util.PaymentTestUtil.payment;
import static star.carsharing.util.PaymentTestUtil.paymentRequestDto;
import static star.carsharing.util.PaymentTestUtil.sessionCreateParams;
import static star.carsharing.util.RentalTestUnit.closedRental;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.exception.unchecked.PaymentException;
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

    @Test
    @DisplayName("Verify method createSession with correct data payment type PAYMENT")
    public void createSession_CorrectDataWithPayment_ReturnPaymentResponseDto() {
        PaymentRequestDto paymentDto = paymentRequestDto(Payment.Type.PAYMENT);
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        BigDecimal amount = BigDecimal.valueOf(246.18);
        SessionCreateParams sessionCreateParams = sessionCreateParams(amount);
        Session session = mockSession();
        Payment payment = mapPaymentRequestDtoToPayment(paymentDto, rental, session, amount);
        PaymentResponseDto expected = mapPaymentToPaymentResponseDto(payment);

        when(rentalRepository.findByIdAndUserId(paymentDto.rentalId(), user.getId()))
                .thenReturn(Optional.of(rental));
        when(stripePaymentService.createSessionParams(amount)).thenReturn(sessionCreateParams);
        when(stripePaymentService.makeSession(sessionCreateParams)).thenReturn(session);
        when(paymentMapper.toModel(paymentDto)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponseDto(payment)).thenReturn(expected);
        PaymentResponseDto actual = paymentService.createSession(user.getId(), paymentDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method createSession with correct data payment type FINE")
    public void createSession_CorrectDataWithFine_ReturnPaymentResponseDto() {
        PaymentRequestDto paymentDto = paymentRequestDto(Payment.Type.FINE);
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        BigDecimal amount = BigDecimal.valueOf(430.815);
        SessionCreateParams sessionCreateParams = sessionCreateParams(amount);
        Session session = mockSession();
        Payment payment = mapPaymentRequestDtoToPayment(paymentDto, rental, session, amount);
        PaymentResponseDto expected = mapPaymentToPaymentResponseDto(payment);

        when(rentalRepository.findByIdAndUserId(paymentDto.rentalId(), user.getId()))
                .thenReturn(Optional.of(rental));
        when(stripePaymentService.createSessionParams(amount)).thenReturn(sessionCreateParams);
        when(stripePaymentService.makeSession(sessionCreateParams)).thenReturn(session);
        when(paymentMapper.toModel(paymentDto)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponseDto(payment)).thenReturn(expected);
        PaymentResponseDto actual = paymentService.createSession(user.getId(), paymentDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method createSession with incorrect data.
             Rental by id and user id not exist
            """)
    public void createSession_IncorrectData_ReturnException() {
        PaymentRequestDto paymentDto = paymentRequestDto(Payment.Type.PAYMENT);
        Long userId = 3L;

        when(rentalRepository.findByIdAndUserId(paymentDto.rentalId(), userId))
                .thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> paymentService.createSession(userId, paymentDto));

        String expected = String.format("Can`t find rental by id %s and user id %s",
                paymentDto.rentalId(), userId);
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method getPaymentById with correct data")
    public void getPaymentById_CorrectData_ReturnPaymentDto() {
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        Payment payment = payment(rental);
        PaymentDto expected = mapPaymentToPaymentDto(payment);

        when(paymentRepository.findById(rental.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);
        PaymentDto actual = paymentService.getPaymentById(payment.getId());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getPaymentById with incorrect data.
             Payment by id is not exist
            """)
    public void getPaymentById_IncorrectData_ReturnException() {
        Long id = 49L;

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> paymentService.getPaymentById(id));

        String expected = "Can`t find payment by id " + id;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method getPayments with correct data")
    public void getPayments_CorrectData_ReturnPageAllPaymentDto() {
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        Payment payment = payment(rental);
        PaymentDto expected = mapPaymentToPaymentDto(payment);
        Pageable pageable = PageRequest.of(0, 10);
        List<Payment> payments = List.of(payment);
        PageImpl<Payment> paymentPage = new PageImpl<>(payments, pageable, payments.size());

        when(paymentRepository.findAllPaymentsByUserId(user.getId(), pageable))
                .thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(expected);
        Page<PaymentDto> pages = paymentService.getPayments(user.getId(), pageable);
        List<PaymentDto> actual = pages.get().toList();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify method paymentCancel with correct data")
    public void paymentCancel_CorrectData_ReturnStatus() {
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        Payment payment = payment(rental);

        when(paymentRepository.findBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));
        paymentService.paymentCancel(payment.getSessionId());

        verify(paymentRepository, times(1)).findBySessionId(payment.getSessionId());
    }

    @Test
    @DisplayName("Verify method paymentCancel with correct data")
    public void paymentCancel_IncorrectData_ReturnException() {
        String sessionId = "cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fO";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> paymentService.paymentCancel(sessionId));

        String expected = "Can`t find session by id " + sessionId;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method paymentSuccess with correct data")
    public void paymentSuccess_CorrectData_ReturnStatus() {
        String sessionId = "cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fOpFtkuPni4lORCcW19wo7";
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        Payment payment = payment(rental);

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripePaymentService.isPaymentSessionPaid(sessionId)).thenReturn(true);
        when(paymentRepository.save(payment)).thenReturn(payment);
        paymentService.paymentSuccess(sessionId);

        verify(paymentRepository, times(1)).findBySessionId(sessionId);
        verify(stripePaymentService, times(1)).isPaymentSessionPaid(any());
        verify(notificationService, times(1)).sentSuccessesPayment(any());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("""
            Verify method paymentSuccess with incorrect data.
             Payment isn`t successful
            """)
    public void paymentSuccess_IncorrectDataPaymentNotPaid_ReturnStatus() {
        String sessionId = "cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fOpFtkuPni4lORCcW19wo7";
        User user = (User) userDetails();
        Car car = car(1L, 1);
        Rental rental = closedRental(user, car);
        Payment payment = payment(rental);

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripePaymentService.isPaymentSessionPaid(sessionId)).thenReturn(false);
        Exception actual = assertThrows(PaymentException.class,
                () -> paymentService.paymentSuccess(sessionId));

        String expected = "Payment isn`t successful for sessionId: " + sessionId;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method paymentSuccess with incorrect data.
             Payment by id is not exist
            """)
    public void paymentSuccess_IncorrectData_ReturnStatus() {
        String sessionId = "cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fOpFtkuPni4lORCcW19wo7";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> paymentService.paymentSuccess(sessionId));

        String expected = "Can`t find session by id " + sessionId;
        assertEquals(expected, actual.getMessage());
    }
}
