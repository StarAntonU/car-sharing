package star.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.dto.car.UpdateCarInventoryDto;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.mapper.CarMapper;
import star.carsharing.model.Car;
import star.carsharing.repository.CarRepository;
import star.carsharing.service.CarService;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto add(CreateCarDto createCarDto) {
        Car car = carMapper.toModel(createCarDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public Page<CarDto> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::toDto);
    }

    @Override
    public CarDto findCarById(Long id) {
        Car car = getCarFromDB(id);
        return carMapper.toDto(car);
    }

    @Override
    public CarDto updateCarById(CreateCarDto carDto, Long id) {
        Car car = getCarFromDB(id);
        carMapper.updateCar(car, carDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarDto updateCarInventory(UpdateCarInventoryDto carDto, Long id) {
        Car car = getCarFromDB(id);
        car.setInventory(carDto.inventory());
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void deleteCarById(Long id) {
        Car car = getCarFromDB(id);
        carRepository.deleteById(id);
    }

    private Car getCarFromDB(Long id) {
        return carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find a car by id " + id));
    }
}
