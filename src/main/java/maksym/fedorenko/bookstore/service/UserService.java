package maksym.fedorenko.bookstore.service;

import maksym.fedorenko.bookstore.dto.user.UserRegistrationRequestDto;
import maksym.fedorenko.bookstore.dto.user.UserResponseDto;
import maksym.fedorenko.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
