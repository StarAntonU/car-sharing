package star.carsharing.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import star.carsharing.annotation.FieldMatch;

@FieldMatch(firstPassName = "password", secondPassName = "repeatedPassword")
public record UpdateUserPassRequestDto(
        @NotBlank
        @Size(min = 8, max = 20)
        String password,
        @NotBlank
        @Size(min = 8, max = 20)
        String repeatedPassword
) {
}
