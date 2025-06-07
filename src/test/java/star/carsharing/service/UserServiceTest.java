package star.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static star.carsharing.util.AuthTestUtil.roleCustomer;
import static star.carsharing.util.AuthTestUtil.user;
import static star.carsharing.util.UserTestUtil.mapUpdateUserPassRequestDtoToUser;
import static star.carsharing.util.UserTestUtil.mapUpdateUserRequestDtoToUser;
import static star.carsharing.util.UserTestUtil.mapUserRegisterRequestDtoToUser;
import static star.carsharing.util.UserTestUtil.mapUserToUserDto;
import static star.carsharing.util.UserTestUtil.updateUserPassRequestDto;
import static star.carsharing.util.UserTestUtil.updateUserRequestDto;
import static star.carsharing.util.UserTestUtil.updateUserRoleRequestDto;
import static star.carsharing.util.UserTestUtil.userRegisterDto;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import star.carsharing.dto.user.UpdateUserPassRequestDto;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UpdateUserRoleRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.exception.checked.RegistrationException;
import star.carsharing.exception.unchecked.EntityNotFoundException;
import star.carsharing.mapper.UserMapper;
import star.carsharing.model.Role;
import star.carsharing.model.User;
import star.carsharing.repository.RoleRepository;
import star.carsharing.repository.UserRepository;
import star.carsharing.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Verify method register with correct data")
    public void register_CorrectData_ReturnUserDto() throws RegistrationException {
        UserRegisterRequestDto userRegisterDto = userRegisterDto();
        User user = mapUserRegisterRequestDtoToUser(userRegisterDto);
        String codePass = passwordEncoder.encode(user.getPassword());
        Role role = roleCustomer();
        UserDto expected = mapUserToUserDto(user,1L);

        when(userRepository.existsByEmail(userRegisterDto.email())).thenReturn(false);
        when(userMapper.toModel(userRegisterDto)).thenReturn(user);
        when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserDto actual = userService.register(userRegisterDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method register with incorrect data.
             User is exist
            """)
    public void register_IncorrectDataUserIsExist_ReturnException() {
        UserRegisterRequestDto userRegisterDto = userRegisterDto();

        when(userRepository.existsByEmail(userRegisterDto.email())).thenReturn(true);
        Exception actual = assertThrows(RegistrationException.class,
                () -> userService.register(userRegisterDto));

        String expected = String.format("User with email %s is exist", userRegisterDto.email());
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method register with incorrect data.
             Role is exist
            """)
    public void register_IncorrectDataRoleIsExist_ReturnException() {
        UserRegisterRequestDto userRegisterDto = userRegisterDto();
        User user = mapUserRegisterRequestDtoToUser(userRegisterDto);
        String codePass = passwordEncoder.encode(user.getPassword());

        when(userMapper.toModel(userRegisterDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(codePass);
        when(userRepository.existsByEmail(userRegisterDto.email())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.register(userRegisterDto));

        String expected = "Can`t find role " + Role.RoleName.CUSTOMER;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method getAllUsers with correct data")
    public void getAllUsers_CorrectData_ReturnPageUserDto() {
        User user = user(2L, roleCustomer());
        UserDto expected = mapUserToUserDto(user, 1L);
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(user);
        PageImpl<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(expected);
        Page<UserDto> pages = userService.getAllUsers(pageable);
        List<UserDto> actual = pages.get().toList();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify method updateUserRole with correct data")
    public void updateUserRole_CorrectData_ReturnPageUserDto() {
        User user = user(2L, roleCustomer());
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(
                Role.RoleName.CUSTOMER);
        Role role = roleCustomer();
        UserDto expected = mapUserToUserDto(user, 1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserDto actual = userService.updateUserRole(user.getId(), updateUserRoleDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify method updateUserRole with incorrect data.
             User is not exist
            """)
    public void updateUserRole_IncorrectDataUserNotExist_ReturnException() {
        Long userId = 86L;
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(
                Role.RoleName.CUSTOMER);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(userId, updateUserRoleDto));

        String expected = "Can`t find user by id " + userId;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("""
            Verify method updateUserRole with incorrect data.
             Role is not exist
            """)
    public void updateUserRole_IncorrectDataRoleNotExist_ReturnException() {
        User user = user(2L, roleCustomer());
        UpdateUserRoleRequestDto updateUserRoleDto = updateUserRoleRequestDto(
                Role.RoleName.CUSTOMER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.empty());
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(user.getId(), updateUserRoleDto));

        String expected = "Can`t find role by name " + Role.RoleName.CUSTOMER;
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify method findUserById with correct data")
    public void findUserById_CorrectData_ReturnUserDto() {
        User user = user(2L, roleCustomer());
        UserDto expected = mapUserToUserDto(user, 1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);
        UserDto actual = userService.findUserById(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method updateUser with correct data")
    public void updateUser_CorrectData_ReturnUserDto() {
        UpdateUserRequestDto updateUserDto = updateUserRequestDto();
        User user = mapUpdateUserRequestDtoToUser(updateUserDto);
        UserDto expected = mapUserToUserDto(user, 1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserDto actual = userService.updateUser(user.getId(), updateUserDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify method updateUserPass with correct data")
    public void updateUserPass_CorrectData_ReturnStatus() {
        UpdateUserPassRequestDto updateUserPassDto = updateUserPassRequestDto();
        User user = mapUpdateUserPassRequestDtoToUser(updateUserPassDto);
        String codePass = passwordEncoder.encode(user.getPassword());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(codePass);
        when(userRepository.save(user)).thenReturn(user);
        userService.updateUserPass(user.getId(), updateUserPassDto);

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
    }
}

