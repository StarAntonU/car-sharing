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
                LocalDate.now().plusDays(1),
                carId
        );
    }

    public static CreateRentalRequestDto invalidCreateRentalRequestDto(Long carId) {
        return new CreateRentalRequestDto(
                LocalDate.now(),
                carId
        );
    }

    public static RentalResponseDto rentalResponseDto(Long id, Boolean isActive) {
        return new RentalResponseDto(
                id,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                1L,
                isActive
        );
    }

    public static Rental rental(User user, Car car, boolean isActive) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(1));
        rental.setUser(user);
        rental.setCar(car);
        rental.setIsActive(isActive);
        return rental;
    }

    public static Rental closedRental(User user, Car car) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(1));
        rental.setActualReturnDate(LocalDate.now().plusDays(2));
        rental.setUser(user);
        rental.setCar(car);
        rental.setIsActive(false);
        return rental;
    }

    public static RentalResponseWithActualReturnDateDto rentalResponseWithActualReturnDateDto() {
        return new RentalResponseWithActualReturnDateDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now(),
                1L
        );
    }

    public static UserRentalIsActiveRequestDto userRentalIsActiveRequestDto(
            Long id, boolean isActive) {
        return new UserRentalIsActiveRequestDto(
                id,
                isActive
        );
    }

    public static RentalResponseDto mapCreateRentalRequestDtoToRentalResponseDto(
            CreateRentalRequestDto createDto) {
        return new RentalResponseDto(
                2L,
                LocalDate.now(),
                createDto.returnDate(),
                1L,
                true
        );
    }

    public static Rental mapCreateRentalRequestDtoToRental(
            CreateRentalRequestDto dto,
            User user,
            Car car) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now());
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
                LocalDate.now().plusDays(1),
                rental.getCar().getId()
        );
    }
}
