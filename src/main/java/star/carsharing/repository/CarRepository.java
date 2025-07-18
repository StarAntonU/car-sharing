package star.carsharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import star.carsharing.model.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
}
