package star.carsharing.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import star.carsharing.telegram.NotificationService;

@TestConfiguration
public class TestNotificationConfig {
    @Bean
    public NotificationService notificationService() {
        return Mockito.mock(NotificationService.class);
    }
}
