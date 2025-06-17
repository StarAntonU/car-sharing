package star.carsharing.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import star.carsharing.model.Car;

public record CreateCarDto(
        @NotBlank
        String model,
        @NotBlank
        String brand,
        @NotNull
        Car.Type type,
        @Positive
        int inventory,
        @NotNull
        @Positive
        BigDecimal dailyFee
) {
}
