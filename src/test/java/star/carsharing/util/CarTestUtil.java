package star.carsharing.util;

import java.math.BigDecimal;
import star.carsharing.dto.car.CarDto;
import star.carsharing.dto.car.CreateCarDto;
import star.carsharing.dto.car.UpdateCarInventoryDto;
import star.carsharing.model.Car;

public class CarTestUtil {
    public static CreateCarDto createCarDto() {
        return new CreateCarDto(
                "X5",
                "BMW",
                Car.Type.HATCHBACK,
                1,
                BigDecimal.valueOf(123.09)
        );
    }

    public static Car car(Long carId, int inventory) {
        Car car = new Car();
        car.setId(carId);
        car.setModel("X5");
        car.setBrand("BMW");
        car.setType(Car.Type.HATCHBACK);
        car.setInventory(inventory);
        car.setDailyFee(BigDecimal.valueOf(123.09));
        return car;
    }

    public static UpdateCarInventoryDto updateCarInventoryDto(int inventory) {
        return new UpdateCarInventoryDto(
                inventory
        );
    }

    public static Car mapCreateCarDtoToCar(CreateCarDto createCarDto, Long carId) {
        Car car = new Car();
        car.setId(carId);
        car.setModel(createCarDto.model());
        car.setBrand(createCarDto.brand());
        car.setType(createCarDto.type());
        car.setInventory(createCarDto.inventory());
        car.setDailyFee(createCarDto.dailyFee());
        return car;
    }

    public static CarDto mapCarToCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType(),
                car.getInventory(),
                car.getDailyFee()
        );
    }

    public static Car mapCarUpdateCarInventoryToCar(UpdateCarInventoryDto dto, Long carId) {
        Car car = new Car();
        car.setId(carId);
        car.setModel("X5");
        car.setBrand("BMW");
        car.setType(Car.Type.HATCHBACK);
        car.setInventory(dto.inventory());
        car.setDailyFee(BigDecimal.valueOf(123.09));
        return car;
    }
}
