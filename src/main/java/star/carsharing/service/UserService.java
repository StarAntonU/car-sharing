package star.carsharing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.carsharing.dto.user.UpdateUserPassRequestDto;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UpdateUserRoleRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.exception.checked.RegistrationException;

public interface UserService {
    UserDto register(UserRegisterRequestDto requestDto) throws RegistrationException;

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserDto findUserById(Long id);

    UserDto updateUser(Long id, UpdateUserRequestDto requestDto);

    void updateUserPass(Long id, UpdateUserPassRequestDto requestDto);
}
