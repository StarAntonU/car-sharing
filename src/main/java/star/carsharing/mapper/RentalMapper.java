package star.carsharing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import star.carsharing.config.MapperConfig;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.model.Rental;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    Rental toModel(CreateRentalRequestDto requestDto);

    @Mapping(target = "carId", source = "car.id")
    RentalResponseDto toResponseDto(Rental rental);

    @Mapping(target = "carId", source = "car.id")
    RentalResponseWithActualReturnDateDto toDtoWithActualReturnDate(Rental rental);
}
