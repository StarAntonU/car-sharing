package star.carsharing.util.impl;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import star.carsharing.util.TimeProvider;

@Service
public class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDate now() {
        return LocalDate.now();
    }
}
