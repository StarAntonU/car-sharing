package star.carsharing.dto.rental;

import java.time.LocalDate;

public record RentalResponseDto(
        Long id,
        LocalDate rentalDate,
        LocalDate returnDate,
        Long carId,
        Boolean isActive
) {
}
