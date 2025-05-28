package star.carsharing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import star.carsharing.config.MapperConfig;
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.model.Car;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarDto carDto);

    void updateCar(@MappingTarget Car car, CreateCarDto carDto);
}
