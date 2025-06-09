package star.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import star.carsharing.dto.user.UpdateUserPassRequestDto;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UpdateUserRoleRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.model.User;
import star.carsharing.service.UserService;

@Tag(name = "User", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/all")
    @Operation(summary = "View list users", description = "View list of all users")
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update role", description = "Update user`s role")
    public UserDto updateUserRole(
            @PathVariable Long id, @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        return userService.updateUserRole(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "View the user", description = "View the user information")
    public UserDto getUserById(Authentication authentication) {
        return userService.findUserById(getUserId(authentication));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the user", description = "Update the user")
    public UserDto updateUser(
            Authentication authentication, @RequestBody @Valid UpdateUserRequestDto requestDto) {
        return userService.updateUser(getUserId(authentication), requestDto);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update pass", description = "Update user`s password")
    public void updateUserPass(
            Authentication authentication,
            @RequestBody @Valid UpdateUserPassRequestDto requestDto) {
        userService.updateUserPass(getUserId(authentication), requestDto);
    }

    private Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
