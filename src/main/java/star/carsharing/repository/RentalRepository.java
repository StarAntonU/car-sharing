package star.carsharing.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import star.carsharing.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUserId(Long userId);

    Page<Rental> findAllByUserIdAndIsActive(Long userId, Boolean isActive, Pageable pageable);

    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);

    Boolean existsByUserIdAndIsActiveIsTrue(Long userId);

    List<Rental> findAllByReturnDateLessThan(LocalDate date);

    List<Rental> findAllByReturnDateGreaterThanEqual(LocalDate date);
}
