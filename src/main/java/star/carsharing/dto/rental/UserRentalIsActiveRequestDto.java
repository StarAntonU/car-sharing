package star.carsharing.dto.rental;

public record UserRentalIsActiveRequestDto(
        Long userId,
        Boolean isActive
) {
}
