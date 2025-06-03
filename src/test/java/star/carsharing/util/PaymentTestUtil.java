package star.carsharing.util;

import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.model.Payment;

public class PaymentTestUtil {
    public static PaymentRequestDto paymentRequestDto(Payment.Type type) {
        return new PaymentRequestDto(
                1L,
                type
        );
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
}
