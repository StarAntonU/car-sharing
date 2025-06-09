package star.carsharing.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import star.carsharing.security.AuthenticationService;

@TestConfiguration
public class TestAuthenticationServiceConfig {
    @Bean
    public AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }
}
