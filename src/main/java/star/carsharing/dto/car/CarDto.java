package star.carsharing.dto.car;

import java.math.BigDecimal;
import star.carsharing.model.Car;

public record CarDto(
        Long id,
        String model,
        String brand,
        Car.Type type,
        int inventory,
        BigDecimal dailyFee
) {
}

