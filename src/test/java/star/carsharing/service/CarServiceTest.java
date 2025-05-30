package star.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static star.carsharing.util.CarTestUtil.car;
import static star.carsharing.util.CarTestUtil.createCarDto;
import static star.carsharing.util.CarTestUtil.mapCarToCarDto;
import static star.carsharing.util.CarTestUtil.mapCarUpdateCarInventoryToCar;
import static star.carsharing.util.CarTestUtil.mapCreateCarDtoToCar;
import static star.carsharing.util.CarTestUtil.updateCarInventoryDto;

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
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.dto.car.UpdateCarInventoryDto;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.mapper.CarMapper;
import star.carsharing.model.Car;
import star.carsharing.repository.CarRepository;
import star.carsharing.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @Test
    @DisplayName("Verify method add with correct data")
    public void add_CorrectData_ReturnValidCarDto() {
        CreateCarDto createCarDto = createCarDto();
        Car car = mapCreateCarDtoToCar(createCarDto, 1L);
        CarDto expected = mapCarToCarDto(car);

        when(carMapper.toModel(createCarDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(expected);
        CarDto actual = carService.add(createCarDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method getAllCars with correct data")
    public void getAllCars_CorrectData_ReturnPageAllCarsDto() {
        Car car = car(1L);
        CarDto expected = mapCarToCarDto(car);
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> cars = List.of(car);
        PageImpl<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car)).thenReturn(expected);
        Page<CarDto> pages = carService.getAllCars(pageable);
        List<CarDto> actual = pages.get().toList();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify method findCarById with correct data")
    public void findCarById_CorrectData_ReturnValidCarDto() {
        Long carId = 1L;
        Car car = car(carId);
        CarDto expected = mapCarToCarDto(car);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expected);
        CarDto actual = carService.findCarById(carId);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method findCarById with incorrect data.
            Car with the id  not exist
            """)
    public void findCarById_IncorrectData_ReturnException() {
        Long carId = 325L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> carService.findCarById(carId)
        );

        String expected = "Can`t find a car by id " + carId;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method updateCarById with correct data")
    public void updateCarById_CorrectData_ReturnValidCarDto() {
        Long carId = 1L;
        CreateCarDto createCarDto = createCarDto();
        Car car = mapCreateCarDtoToCar(createCarDto, carId);
        CarDto expected = mapCarToCarDto(car);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(expected);
        CarDto actual = carService.updateCarById(createCarDto, carId);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method updateCarInventory with correct data")
    public void updateCarInventory_CorrectData_ReturnValidCarDto() {
        UpdateCarInventoryDto updateCarInventoryDto = updateCarInventoryDto(2);
        Long carId = 1L;
        Car car = mapCarUpdateCarInventoryToCar(updateCarInventoryDto, carId);
        CarDto expected = mapCarToCarDto(car);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(expected);
        CarDto actual = carService.updateCarInventory(updateCarInventoryDto, carId);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method deleteCarById with correct data")
    public void deleteCarById_CorrectData_NoReturn() {
        Long carId = 1L;
        Car car = car(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        carService.deleteCarById(carId);

        verify(carRepository, times(1)).findById(carId);
        verify(carRepository, times(1)).deleteById(carId);
    }
}
