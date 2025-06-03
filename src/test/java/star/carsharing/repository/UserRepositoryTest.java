package star.carsharing.repository;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static star.carsharing.util.UserTestUtil.userManager;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import star.carsharing.model.Role;
import star.carsharing.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify custom sql query findAllByRoles with correct data")
    @Sql(scripts = {"classpath:db/user/add-users-to-users-table.sql",
            "classpath:db/usersroles/add-users-and-roles-to-users_roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/usersroles/delete-users-and-roles-to-users_roles.sql",
            "classpath:db/user/delete-users-to-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByRoles_CorrectData_ReturnListRoles() {
        List<User> actual = userRepository.findAllByRoles(Role.RoleName.MANAGER);

        User expected = userManager();
        assertEquals(1, actual.size());
        assertTrue(reflectionEquals(expected, actual.get(0)));
    }
}
