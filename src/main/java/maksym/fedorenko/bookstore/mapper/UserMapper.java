package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.config.MapperConfig;
import maksym.fedorenko.bookstore.dto.user.UserRegistrationRequestDto;
import maksym.fedorenko.bookstore.dto.user.UserResponseDto;
import maksym.fedorenko.bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toUser(UserRegistrationRequestDto request);

    UserResponseDto toDto(User user);
}
