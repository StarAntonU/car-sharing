package star.carsharing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.exception.checked.NotificationException;

public interface RentalService {
    RentalResponseDto createRental(
            Authentication authentication, CreateRentalRequestDto requestDto) throws NotificationException;

    RentalResponseDto getRentalById(Long userId, Long rentalId);

    RentalResponseWithActualReturnDateDto closeRental(Long userId, Long rentalId);

    Page<RentalResponseDto> getUserRentalIsActive(
            UserRentalIsActiveRequestDto requestDto, Pageable pageable);
}
