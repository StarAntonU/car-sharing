package star.carsharing.util;

import java.time.LocalDate;

public interface TimeProvider {
    LocalDate now();
}
