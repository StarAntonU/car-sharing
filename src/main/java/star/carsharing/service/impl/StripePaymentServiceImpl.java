package star.carsharing.service.impl;

import com.stripe.Stripe;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import star.carsharing.service.StripePaymentService;

@Service
public class StripePaymentServiceImpl implements StripePaymentService {
    private static final String CURRENCY = "usd";
    private static final String PRODUCT_NAME = "Car Rental Payment";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${payment.success.url}")
    private String successUrl;
    @Value("${payment.cancel.url}")
    private String cancelUrl;

    @Override
    public SessionCreateParams createSessionParams(BigDecimal amount) {
        Stripe.apiKey = stripeSecretKey;
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(amount.multiply(
                                                        BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(PRODUCT_NAME)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
