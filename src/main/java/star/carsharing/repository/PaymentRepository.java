package star.carsharing.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import star.carsharing.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
            SELECT p FROM Payment p
            JOIN FETCH p.rental r
            WHERE r.user.id = :userId
            """)
    Page<Payment> findAllPaymentsByUserId(Long userId, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);
}
