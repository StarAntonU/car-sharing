package star.carsharing.util;

import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import star.carsharing.model.Role;
import star.carsharing.model.User;

public class AuthTestUtil {
    public static Authentication authentication() {
        UserDetails userDetails = userDetails();
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public static UserDetails userDetails() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword("12345678");
        user.setTelegramChatId("0987654321");
        user.setRoles(Set.of(roleCustomer()));
        return user;
    }

    public static Role roleCustomer() {
        Role role = new Role();
        role.setName(Role.RoleName.CUSTOMER);
        return role;
    }
}
