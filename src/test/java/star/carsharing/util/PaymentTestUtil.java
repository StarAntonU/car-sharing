package star.carsharing.util;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.model.Payment;
import star.carsharing.model.Rental;

public class PaymentTestUtil {
    public static Session mockSession(String id, String url) {
        Session mockSession = new Session();
        mockSession.setId(id);
        mockSession.setUrl(url);
        return mockSession;
    }

    public static SessionCreateParams sessionCreateParams(BigDecimal amount) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amount.multiply(
                                                        BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName("Car Rental Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .build();
    }

    public static PaymentRequestDto paymentRequestDto(Long rentalId, Payment.Type type) {
        return new PaymentRequestDto(
                rentalId,
                type
        );
    }

    public static Payment paymentStatusPaid(Rental rental) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PAID);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl("https://checkout.stripe.com/c/pay/cs_test_a1OwDVFo");
        payment.setSessionId("cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fOpFtkuPni4lORCcW19wo7");
        payment.setAmount(BigDecimal.valueOf(246.18));
        return payment;
    }

    public static Payment paymentStatusPayment(Rental rental) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl("https://checkout.stripe.com/c/pay/cs_test_a1OwDVFo");
        payment.setSessionId("cs_test_a1OwDVFofk5jpPJzElJ2LBrUDD59a1MlhCf1fOpFtkuPni4lORCcW19wo7");
        payment.setAmount(BigDecimal.valueOf(246.18));
        return payment;
    }

    public static PaymentDto paymentDto() {
        return new PaymentDto(
                1L,
                Payment.Status.PENDING,
                Payment.Type.PAYMENT,
                1L,
                BigDecimal.valueOf(246.24)
        );
    }

    public static PaymentDto mapPaymentToPaymentDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getStatus(),
                payment.getType(),
                payment.getRental().getId(),
                payment.getAmount()
        );
    }

    public static Payment mapPaymentRequestDtoToPayment(
            PaymentRequestDto dto, Rental rental, Session session, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(dto.paymentType());
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmount(amount);
        return payment;
    }

    public static PaymentResponseDto mapPaymentToPaymentResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getSessionId(),
                payment.getSessionUrl()
        );
    }

    public static PaymentResponseDto mapSessionToPaymentResponseDto(Session session) {
        return new PaymentResponseDto(
                session.getId(),
                session.getUrl()
        );
    }
}
