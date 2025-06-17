package star.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import star.carsharing.annotation.FieldMatch;

@FieldMatch(firstPassName = "password", secondPassName = "repeatedPassword")
public record UserRegisterRequestDto(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 20)
        String password,
        @NotBlank
        @Size(min = 8, max = 20)
        String repeatedPassword,
        @NotBlank
        @Size(min = 2, max = 64)
        String firstName,
        @NotBlank
        @Size(min = 2, max = 64)
        String lastName
) {
}
