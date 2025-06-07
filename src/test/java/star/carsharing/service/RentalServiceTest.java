package star.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static star.carsharing.util.AuthTestUtil.authentication;
import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.CarTestUtil.car;
import static star.carsharing.util.RentalTestUnit.createRentalRequestDto;
import static star.carsharing.util.RentalTestUnit.mapCreateRentalRequestDtoToRental;
import static star.carsharing.util.RentalTestUnit.mapRentalToRentalResponseDto;
import static star.carsharing.util.RentalTestUnit.mapRentalToRentalWithActualReturnDateDto;
import static star.carsharing.util.RentalTestUnit.rental;
import static star.carsharing.util.RentalTestUnit.userRentalIsActiveRequestDto;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.exception.unchecked.ForbiddenOperationException;
import star.carsharing.exception.unchecked.InsufficientQuantityException;
import star.carsharing.mapper.RentalMapper;
import star.carsharing.model.Car;
import star.carsharing.model.Rental;
import star.carsharing.model.User;
import star.carsharing.repository.CarRepository;
import star.carsharing.repository.RentalRepository;
import star.carsharing.service.impl.RentalServiceImpl;
import star.carsharing.telegram.NotificationService;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("Verify method createRental with correct data")
    public void createRental_CorrectData_ReturnRentalResponseDto() {
        Authentication authentication = authentication(2L, roleCustomer());
        Long carId = 1L;
        Car car = car(carId, 1);
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(carId);
        User user = (User) authentication.getPrincipal();
        Rental rental = mapCreateRentalRequestDtoToRental(createRentalDto, user, car);
        RentalResponseDto expected = mapRentalToRentalResponseDto(rental);

        when(rentalRepository.existsByUserIdAndIsActiveIsTrue(user.getId())).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalMapper.toModel(createRentalDto)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);
        RentalResponseDto actual = rentalService.createRental(authentication, createRentalDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method createRental with incorrect data.
             Costumer has had a rental
            """)
    public void createRental_IncorrectData_ReturnException() {
        Authentication authentication = authentication(2L, roleCustomer());
        User user = (User) authentication.getPrincipal();
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(1L);

        when(rentalRepository.existsByUserIdAndIsActiveIsTrue(user.getId())).thenReturn(true);
        Exception actual = assertThrows(ForbiddenOperationException.class,
                () -> rentalService.createRental(authentication, createRentalDto)
        );

        String expected = "You already have a rental car";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method createRental with incorrect data.
             Invalid limit is true
            """)
    public void createRental_IncorrectDataInvalidLimitTrue_ReturnException() {
        Authentication authentication = authentication(2L, roleCustomer());
        User user = (User) authentication.getPrincipal();
        Long carId = 1L;
        Car car = car(carId, 0);
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(1L);

        when(rentalRepository.existsByUserIdAndIsActiveIsTrue(user.getId())).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        Exception actual = assertThrows(InsufficientQuantityException.class,
                () -> rentalService.createRental(authentication, createRentalDto)
        );

        String expected = "Insufficient quantity of cars";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method createRental with incorrect data.
             Car with the id not exist
            """)
    public void createRental_IncorrectDataCarByIdNotExist_ReturnException() {
        Authentication authentication = authentication(2L, roleCustomer());
        User user = (User) authentication.getPrincipal();
        Long carId = 345L;
        CreateRentalRequestDto createRentalDto = createRentalRequestDto(carId);

        when(rentalRepository.existsByUserIdAndIsActiveIsTrue(user.getId())).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> rentalService.createRental(authentication, createRentalDto)
        );

        String expected = "Can`t find the car by id " + carId;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method getRentalById with correct data")
    public void getRentalById_CorrectData_ReturnRentalResponseDto() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, true);
        List<Rental> rentals = List.of(rental);
        RentalResponseDto expected = mapRentalToRentalResponseDto(rental);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(rentals);
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);
        RentalResponseDto actual = rentalService.getRentalById(user.getId(), rental.getId());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method getRentalById with incorrect data.
             List rentals is empty
            """)
    public void getRentalById_IncorrectData_ReturnException() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, true);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(List.of());
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        Exception actual = assertThrows(ForbiddenOperationException.class,
                () -> rentalService.getRentalById(user.getId(), rental.getId()));

        String expected = "Access is denied";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method getRentalById with incorrect data.
             Rental is not exist
            """)
    public void getRentalById_IncorrectDataRentalNotExist_ReturnException() {
        Long id = 19L;

        when(rentalRepository.findAllByUserId(id)).thenReturn(List.of());
        when(rentalRepository.findById(id)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> rentalService.getRentalById(id, id));

        String expected = "Can`t find the rental by id " + id;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method closeRental with correct data")
    public void closeRental_CorrectData_ReturnRentalResponseWithActualReturnDateDto() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, true);
        RentalResponseWithActualReturnDateDto expected =
                mapRentalToRentalWithActualReturnDateDto(rental);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(List.of(rental));
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalMapper.toDtoWithActualReturnDate(rental)).thenReturn(expected);
        RentalResponseWithActualReturnDateDto actual =
                rentalService.closeRental(user.getId(), rental.getId());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method closeRental with incorrect data.
             Rental is closed
            """)
    public void closeRental_IncorrectDataRentalClosed_ReturnException() {
        Authentication authentication = authentication(1L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, false);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(List.of(rental));
        when(rentalRepository.findById(user.getId())).thenReturn(Optional.of(rental));
        Exception actual = assertThrows(ForbiddenOperationException.class,
                () -> rentalService.closeRental(user.getId(), rental.getId()));

        String expected = "The rental is closed";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method closeRental with incorrect data.
             Rental is not exist
            """)
    public void closeRental_IncorrectDataRentalNotExist_ReturnException() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, true);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(List.of());
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        Exception actual = assertThrows(ForbiddenOperationException.class,
                () -> rentalService.closeRental(user.getId(), rental.getId()));

        String expected = "Access is denied";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method getUserRentalIsActive with correct data.
             Is active is true
            """)
    public void getUserRentalIsActive_CorrectDataIsActiveTrue_ReturnPageDto() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, true);
        RentalResponseDto expected = mapRentalToRentalResponseDto(rental);
        Pageable pageable = PageRequest.of(0, 10);
        List<Rental> rentals = List.of(rental);
        PageImpl<Rental> rentalPage = new PageImpl<>(rentals, pageable, rentals.size());
        UserRentalIsActiveRequestDto userRentalIsActiveRequestDto =
                userRentalIsActiveRequestDto(user.getId(), true);

        when(rentalRepository.findAllByUserIdAndIsActive(
                user.getId(), userRentalIsActiveRequestDto.isActive(), pageable))
                .thenReturn(rentalPage);
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);
        Page<RentalResponseDto> pages = rentalService
                .getUserRentalIsActive(userRentalIsActiveRequestDto, pageable);
        List<RentalResponseDto> actual = pages.get().toList();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("""
            Verify method getUserRentalIsActive with correct data.
             Is active is false
            """)
    public void getUserRentalIsActive_CorrectDataIsActiveFalse_ReturnPageDto() {
        Authentication authentication = authentication(2L, roleCustomer());
        Car car = car(1L, 1);
        User user = (User) authentication.getPrincipal();
        Rental rental = rental(user, car, false);
        RentalResponseDto expected = mapRentalToRentalResponseDto(rental);
        Pageable pageable = PageRequest.of(0, 10);
        List<Rental> rentals = List.of(rental);
        PageImpl<Rental> rentalPage = new PageImpl<>(rentals, pageable, rentals.size());
        UserRentalIsActiveRequestDto userRentalIsActiveRequestDto =
                userRentalIsActiveRequestDto(user.getId(), false);

        when(rentalRepository.findAllByUserIdAndIsActive(
                user.getId(), userRentalIsActiveRequestDto.isActive(), pageable))
                .thenReturn(rentalPage);
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);
        Page<RentalResponseDto> pages = rentalService
                .getUserRentalIsActive(userRentalIsActiveRequestDto, pageable);
        List<RentalResponseDto> actual = pages.get().toList();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }
}
