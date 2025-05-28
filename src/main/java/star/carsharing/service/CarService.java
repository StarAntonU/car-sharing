package star.carsharing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.dto.car.UpdateCarInventoryDto;

public interface CarService {
    CarDto add(CreateCarDto createCarDto);

    Page<CarDto> getAllCars(Pageable pageable);

    CarDto findCarById(Long id);

    CarDto updateCarById(CreateCarDto carDto, Long id);

    CarDto updateCarInventory(UpdateCarInventoryDto carDto, Long id);

    void deleteCarById(Long id);
}
