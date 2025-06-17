package star.carsharing.util;

import java.math.BigDecimal;
import java.util.List;
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

    public static CarDto carDto(Long id) {
        return new CarDto(
                id,
                "Megan",
                "Renault", Car.Type.HATCHBACK,
                1,
                BigDecimal.valueOf(93.72));
    }

    public static UpdateCarInventoryDto updateCarInventoryDto(int inventory) {
        return new UpdateCarInventoryDto(
                inventory
        );
    }

    public static CreateCarDto invalidCreateCarDto() {
        return new CreateCarDto(
                "",
                "",
                Car.Type.HATCHBACK,
                1,
                BigDecimal.valueOf(34.21)
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

    public static CarDto mapCreateCarDtoToCarDto(CreateCarDto createCarDto, Long id) {
        return new CarDto(
                id,
                createCarDto.model(),
                createCarDto.brand(),
                createCarDto.type(),
                createCarDto.inventory(),
                createCarDto.dailyFee()
        );
    }

    public static CarDto mapUpdateCarInventoryDtoToCarDto(UpdateCarInventoryDto updateDto) {
        return new CarDto(
                2L,
                "Megan",
                "Renault",
                Car.Type.HATCHBACK,
                updateDto.inventory(),
                BigDecimal.valueOf(93.72)
        );
    }

    public static List<CarDto> listThreeCarsDto() {
        CarDto bmw = new CarDto(1L,
                "X5",
                "BMW",
                Car.Type.SUV,
                1,
                BigDecimal.valueOf(123.12)
        );
        CarDto renault = new CarDto(
                2L,
                "Megan",
                "Renault",
                Car.Type.HATCHBACK,
                1,
                BigDecimal.valueOf(93.72)
        );
        CarDto volvo = new CarDto(
                3L,
                "XC90",
                "Volvo",
                Car.Type.UNIVERSAL,
                1,
                BigDecimal.valueOf(154.58)
        );
        return List.of(bmw, renault, volvo);
    }

    public static List<CarDto> listTwoCarsDto() {
        CarDto bmw = new CarDto(
                1L,
                "X5",
                "BMW",
                Car.Type.SUV,
                1,
                BigDecimal.valueOf(123.12)
        );
        CarDto volvo = new CarDto(
                3L,
                "XC90",
                "Volvo",
                Car.Type.UNIVERSAL,
                1,
                BigDecimal.valueOf(154.58));
        return List.of(bmw, volvo);
    }
}
