package star.carsharing.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import star.carsharing.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);
}
