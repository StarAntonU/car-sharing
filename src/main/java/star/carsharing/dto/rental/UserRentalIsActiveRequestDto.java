package star.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserRentalIsActiveRequestDto(
        @Positive
        @NotNull
        Long userId,
        @NotNull
        Boolean isActive
) {
}
