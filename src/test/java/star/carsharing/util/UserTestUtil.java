package star.carsharing.util;

import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.AuthTestUtil.roleManager;

import java.util.List;
import java.util.Set;
import star.carsharing.dto.user.UpdateUserPassRequestDto;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UpdateUserRoleRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.model.Role;
import star.carsharing.model.User;

public class UserTestUtil {
    public static User userManager() {
        User user = new User();
        user.setId(1L);
        user.setEmail("manager@email.com");
        user.setFirstName("manager");
        user.setLastName("manager");
        user.setPassword("$2a$12$wdHo5USWTk.k9vdFPupg6uGSzBzxXfTS/Gy2D1ijzxzpoYZ39GZL6");
        user.setTelegramChatId("1234567890");
        user.setRoles(Set.of(roleManager()));
        return user;
    }

    public static UserRegisterRequestDto userRegisterDto() {
        return new UserRegisterRequestDto(
                "test@email.com",
                "12345678",
                "12345678",
                "UserName",
                "UserSurname",
                "0987654321"
        );
    }

    public static UpdateUserRequestDto updateUserRequestDto() {
        return new UpdateUserRequestDto(
                "user@email.com",
                "UserName",
                "UserSurname",
                "0987654321"
        );
    }

    public static UpdateUserRequestDto invalidUpdateUserRequestDto() {
        return new UpdateUserRequestDto(
                "useremail.com",
                "",
                "",
                "0987654321"
        );
    }

    public static UpdateUserPassRequestDto updateUserPassRequestDto() {
        return new UpdateUserPassRequestDto(
                "12345678",
                "12345678"
        );
    }

    public static UpdateUserPassRequestDto invalidUpdateUserPassRequestDto() {
        return new UpdateUserPassRequestDto(
                "1234",
                "1234"
        );
    }

    public static UpdateUserRoleRequestDto updateUserRoleRequestDto(Role.RoleName role) {
        return new UpdateUserRoleRequestDto(
                role
        );
    }

    public static UserDto updateRoleRequestDtoToUserDtoRoleManager() {
        UserDto user = new UserDto();
        user.setId(2L);
        user.setEmail("user@email.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setRolesId(List.of(1L));
        return user;
    }

    public static UserDto mapUpdateUserRequestDtoToUserDto(UpdateUserRequestDto updateDto) {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setEmail(updateDto.email());
        userDto.setFirstName(updateDto.firstName());
        userDto.setLastName(updateDto.lastName());
        userDto.setRolesId(List.of(2L));
        return userDto;
    }

    public static User mapUpdateUserPassRequestDtoToUser(UpdateUserPassRequestDto dto) {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword(dto.password());
        user.setTelegramChatId("0987654321");
        user.setRoles(Set.of(roleCustomer()));
        return user;
    }

    public static User mapUpdateUserRequestDtoToUser(UpdateUserRequestDto dto) {
        User user = new User();
        user.setId(1L);
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPassword("12345678");
        user.setTelegramChatId(dto.telegramChatId());
        user.setRoles(Set.of(roleCustomer()));
        return user;
    }

    public static User mapUserRegisterRequestDtoToUser(UserRegisterRequestDto dto) {
        User user = new User();
        user.setId(1L);
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPassword(dto.password());
        user.setTelegramChatId(dto.telegramChatId());
        user.setRoles(Set.of(roleCustomer()));
        return user;
    }

    public static UserDto mapUserToUserDto(User user, Long roleId) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRolesId(List.of(roleId));
        return userDto;
    }

    public static List<UserDto> listThreeUsersDto() {
        UserDto manager = new UserDto();
        manager.setId(1L);
        manager.setEmail("manager@email.com");
        manager.setFirstName("manager");
        manager.setLastName("manager");
        manager.setRolesId(List.of(1L));

        UserDto user = new UserDto();
        user.setId(2L);
        user.setEmail("user@email.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setRolesId(List.of(2L));

        UserDto user2 = new UserDto();
        user2.setId(3L);
        user2.setEmail("user2@email.com");
        user2.setFirstName("user2");
        user2.setLastName("user2");
        user2.setRolesId(List.of(2L));
        return List.of(manager, user, user2);
    }
}
