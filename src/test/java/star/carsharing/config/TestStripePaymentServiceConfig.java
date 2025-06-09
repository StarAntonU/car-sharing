package star.carsharing.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import star.carsharing.service.StripePaymentService;

@TestConfiguration
public class TestStripePaymentServiceConfig {
    @Bean
    public StripePaymentService stripePaymentService() {
        return Mockito.mock(StripePaymentService.class);
    }
}
