package star.carsharing.util;

import java.time.LocalDate;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.model.Car;
import star.carsharing.model.Rental;
import star.carsharing.model.User;

public class RentalTestUnit {
    public static CreateRentalRequestDto createRentalRequestDto(Long carId) {
        return new CreateRentalRequestDto(
                LocalDate.of(2025, 5, 17),
                carId
        );
    }

    public static Rental rental(User user, Car car, boolean isActive) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2025, 5, 15));
        rental.setReturnDate(LocalDate.of(2025, 5, 17));
        rental.setUser(user);
        rental.setCar(car);
        rental.setIsActive(isActive);
        return rental;
    }

    public static UserRentalIsActiveRequestDto userRentalIsActiveRequestDto(
            Long id, boolean isActive) {
        return new UserRentalIsActiveRequestDto(
                id,
                isActive
        );
    }

    public static Rental mapCreateRentalRequestDtoToRental(
            CreateRentalRequestDto dto,
            User user,
            Car car) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2025, 5, 15));
        rental.setReturnDate(dto.returnDate());
        rental.setUser(user);
        rental.setCar(car);
        rental.setIsActive(true);
        return rental;
    }

    public static RentalResponseDto mapRentalToRentalResponseDto(Rental rental) {
        return new RentalResponseDto(
                rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getCar().getId(),
                rental.getIsActive()
        );
    }

    public static RentalResponseWithActualReturnDateDto mapRentalToRentalWithActualReturnDateDto(
            Rental rental) {
        return new RentalResponseWithActualReturnDateDto(rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                LocalDate.of(2025, 5, 16),
                rental.getCar().getId()
        );
    }
}
