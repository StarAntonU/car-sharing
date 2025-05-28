package star.carsharing.dto.car;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCarInventoryDto(
        @Positive
        @NotNull
        int inventory
) {
}
