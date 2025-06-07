package star.carsharing.util;

import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import star.carsharing.model.Role;
import star.carsharing.model.User;

public class AuthTestUtil {
    public static Authentication authentication(Long userId, Role role) {
        User user = user(userId, role);
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    public static User user(Long userId, Role role) {
        User user = new User();
        user.setId(userId);
        user.setEmail("user@email.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword("12345678");
        user.setTelegramChatId("0987654321");
        user.setRoles(Set.of(role));
        return user;
    }

    public static Role roleCustomer() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.RoleName.CUSTOMER);
        return role;
    }

    public static Role roleManager() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.MANAGER);
        return role;
    }
}
