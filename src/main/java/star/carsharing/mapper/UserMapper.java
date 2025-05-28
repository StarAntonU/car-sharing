package star.carsharing.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import star.carsharing.config.MapperConfig;
import star.carsharing.dto.user.UpdateUserRequestDto;
import star.carsharing.dto.user.UserDto;
import star.carsharing.dto.user.UserRegisterRequestDto;
import star.carsharing.model.Role;
import star.carsharing.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegisterRequestDto requestDto);

    @Mapping(target = "rolesId", ignore = true)
    UserDto toDto(User user);

    @AfterMapping
    default void setRolesId(@MappingTarget UserDto responseDto, User user) {
        List<Long> rolesId = user.getRoles().stream()
                .map(Role::getId)
                .toList();
        responseDto.setRolesId(rolesId);
    }

    void updateUser(@MappingTarget User user, UpdateUserRequestDto requestDto);
}
