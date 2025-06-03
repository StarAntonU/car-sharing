package star.carsharing.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;

public interface StripePaymentService {
    Session makeSession(SessionCreateParams params);

    SessionCreateParams createSessionParams(BigDecimal amount);
}
