package star.carsharing.service.impl;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import star.carsharing.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDto register(UserRegisterRequestDto requestDto)
            throws RegistrationException {
        checkIfUserExists(requestDto);
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Can`t find role " + Role.RoleName.CUSTOMER));
        user.setRoles(addRole(role));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override

    public UserDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto) {
        User user = getUserFromDbById(id);
        Role role = roleRepository.findByName(requestDto.role()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find role by name " + requestDto.role())
        );
        user.setRoles(addRole(role));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto findUserById(Long id) {
        return userMapper.toDto(getUserFromDbById(id));
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserRequestDto requestDto) {
        User user = getUserFromDbById(id);
        userMapper.updateUser(user, requestDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void updateUserPass(Long id, UpdateUserPassRequestDto requestDto) {
        User user = getUserFromDbById(id);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        userRepository.save(user);
    }

    @Override
    public void setUserTelegramChatId(String username, String chatId) {
        User user = getUserFromDbByEmail(username);
        user.setTelegramChatId(chatId);
        userRepository.save(user);
    }

    private void checkIfUserExists(UserRegisterRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RegistrationException(
                    String.format("User with email %s is exist", requestDto.email()));
        }
    }

    private Set<Role> addRole(Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return roles;
    }

    private User getUserFromDbById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user by id " + id)
        );
    }

    private User getUserFromDbByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user by id " + email)
        );
    }
}
