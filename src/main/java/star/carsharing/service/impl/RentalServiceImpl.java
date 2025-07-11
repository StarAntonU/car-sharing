package star.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.exception.checked.NotificationException;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.exception.unchecked.ForbiddenOperationException;
import star.carsharing.exception.unchecked.InsufficientQuantityException;
import star.carsharing.mapper.RentalMapper;
import star.carsharing.model.Car;
import star.carsharing.model.Rental;
import star.carsharing.model.User;
import star.carsharing.repository.CarRepository;
import star.carsharing.repository.RentalRepository;
import star.carsharing.service.RentalService;
import star.carsharing.telegram.NotificationService;
import star.carsharing.util.TimeProvider;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final boolean ACTIVE = true;
    private static final boolean INACTIVE = false;
    private static final int INVALID_LIMIT = 0;

    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;
    private final TimeProvider timeProvider;

    @Override
    public RentalResponseDto createRental(
            Authentication authentication, CreateRentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        if (rentalRepository.existsByUserIdAndIsActiveIsTrue(user.getId())) {
            throw new ForbiddenOperationException("You already have a rental car");
        }
        Car car = getCarFromDB(requestDto.carId());
        if (car.getInventory() == INVALID_LIMIT) {
            throw new InsufficientQuantityException(
                    "Insufficient quantity of cars");
        }
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setRentalDate(timeProvider.now());
        rental.setReturnDate(requestDto.returnDate());
        rental.setIsActive(ACTIVE);
        rental.setUser(user);
        car.setInventory(car.getInventory() - 1);
        rental.setCar(car);
        try {
            notificationService.sentNotificationCreateRental(rental);
        } catch (NotificationException e) {
            log.info("Can`t send the notification to user by id {}", user.getId());
        }
        return rentalMapper.toResponseDto(rentalRepository.save(rental));
    }

    @Override
    public RentalResponseDto getRentalById(Long userId, Long rentalId) {
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        Rental rental = getRentalFromDB(rentalId);
        if (!rentals.contains(rental)) {
            throw new ForbiddenOperationException("Access is denied");
        }
        return rentalMapper.toResponseDto(rental);
    }

    @Override
    public RentalResponseWithActualReturnDateDto closeRental(Long userId, Long rentalId) {
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        Rental rental = getRentalFromDB(rentalId);
        if (!rental.getIsActive()) {
            throw new ForbiddenOperationException("The rental is closed");
        }
        if (!rentals.contains(rental)) {
            throw new ForbiddenOperationException("Access is denied");
        }
        rental.setIsActive(INACTIVE);
        rental.setActualReturnDate(timeProvider.now());
        try {
            notificationService.sentNotificationClosedRental(rental);
        } catch (NotificationException e) {
            log.info("Can`t send the notification to user by id {}", userId);
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalMapper.toDtoWithActualReturnDate(rental);
    }

    @Override
    public Page<RentalResponseDto> getUserRentalIsActive(
            UserRentalIsActiveRequestDto requestDto, Pageable pageable) {
        return rentalRepository
                .findAllByUserIdAndIsActive(requestDto.userId(), requestDto.isActive(), pageable)
                .map(rentalMapper::toResponseDto);

    }

    private Car getCarFromDB(Long id) {
        return carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find the car by id " + id)
        );
    }

    private Rental getRentalFromDB(Long id) {
        return rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find the rental by id " + id)
        );
    }
}
