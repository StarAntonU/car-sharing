package star.carsharing.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.exception.unchecked.PaymentException;
import star.carsharing.mapper.PaymentMapper;
import star.carsharing.model.Payment;
import star.carsharing.model.Rental;
import star.carsharing.repository.PaymentRepository;
import star.carsharing.repository.RentalRepository;
import star.carsharing.service.PaymentService;
import star.carsharing.service.StripePaymentService;
import star.carsharing.telegram.NotificationService;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.5");
    private static final String STATUS_PAY = "paid";
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripePaymentService stripePaymentService;
    private final NotificationService notificationService;

    @Override
    public PaymentResponseDto createSession(Long userId, PaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findByIdAndUserId(requestDto.rentalId(), userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Can`t find rental by id %s and user id %s",
                                        requestDto.rentalId(), userId))
                );
        BigDecimal amount = calculateAmount(rental, requestDto.paymentType());
        SessionCreateParams sessionCreateParams = stripePaymentService.createSessionParams(amount);
        Session session = stripePaymentService.makeSession(sessionCreateParams);

        Payment payment = paymentMapper.toModel(requestDto);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(requestDto.paymentType());
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmount(amount);
        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment by id " + id)
        );
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public Page<PaymentDto> getPayments(Long userId, Pageable pageable) {
        return paymentRepository.findAllPaymentsByUserId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    public void paymentSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find session by id " + sessionId)
        );
        if (!isPaymentSessionPaid(sessionId)) {
            throw new PaymentException("Payment isn`t successful for sessionId: " + sessionId);
        }
        payment.setStatus(Payment.Status.PAID);
        notificationService.sentSuccessesPayment(payment);
        paymentRepository.save(payment);
    }

    @Override
    public void paymentCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find session by id " + sessionId)
        );
        if (payment.getStatus().equals(Payment.Status.PENDING)) {
            notificationService.sentCancelPayment(payment);
        }
    }

    private BigDecimal calculateAmount(Rental rental, Payment.Type type) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getActualReturnDate());
        BigDecimal amount = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));
        if (type == Payment.Type.FINE) {
            long fineDays = ChronoUnit.DAYS.between(
                    rental.getReturnDate(), rental.getActualReturnDate());
            BigDecimal fineAmount = FINE_MULTIPLIER.multiply(
                    rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(fineDays)));
            amount = amount.add(fineAmount);
        }
        return amount;
    }

    private boolean isPaymentSessionPaid(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return STATUS_PAY.equals(session.getPaymentStatus());
        } catch (StripeException e) {
            throw new RuntimeException("Can`t retrieve Stripe session by id " + sessionId, e);
        }
    }
}
