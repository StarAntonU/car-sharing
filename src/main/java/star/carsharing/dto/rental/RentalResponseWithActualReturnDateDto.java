package star.carsharing.dto.rental;

import java.time.LocalDate;

public record RentalResponseWithActualReturnDateDto(
        Long id,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        Long carId
) {
}
