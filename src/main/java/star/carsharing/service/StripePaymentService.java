package star.carsharing.service;

import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;

public interface StripePaymentService {
    SessionCreateParams createSessionParams(BigDecimal amount);
}
