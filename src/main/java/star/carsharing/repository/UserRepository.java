package star.carsharing.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import star.carsharing.model.Role;
import star.carsharing.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            FROM User u
            LEFT JOIN u.roles r
            WHERE r.name = :roleName
            """)
    List<User> findAllByRoles(Role.RoleName roleName);
}
