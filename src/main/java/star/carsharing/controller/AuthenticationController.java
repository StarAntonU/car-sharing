package star.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserLoginRequestDto;
import star.carsharing.dto.user.UserLoginResponseDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.exception.checked.RegistrationException;
import star.carsharing.security.AuthenticationService;
import star.carsharing.service.UserService;

@Tag(name = "Authentication", description = "Endpoints for authentication users")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    @Operation(summary = "register user", description = "registration a new user")
    public UserDto register(@RequestBody @Valid UserRegisterRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "login user", description = "Check if user is authenticated")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
