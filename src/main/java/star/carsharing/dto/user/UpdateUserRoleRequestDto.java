package star.carsharing.dto.user;

import jakarta.validation.constraints.NotNull;
import star.carsharing.model.Role;

public record UpdateUserRoleRequestDto(
        @NotNull
        Role.RoleName role
) {
}
