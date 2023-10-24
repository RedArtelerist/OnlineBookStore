package maksym.fedorenko.bookstore.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.user.UserRegistrationRequestDto;
import maksym.fedorenko.bookstore.dto.user.UserResponseDto;
import maksym.fedorenko.bookstore.exception.RegistrationException;
import maksym.fedorenko.bookstore.mapper.UserMapper;
import maksym.fedorenko.bookstore.model.Role;
import maksym.fedorenko.bookstore.model.RoleName;
import maksym.fedorenko.bookstore.model.User;
import maksym.fedorenko.bookstore.repository.RoleRepository;
import maksym.fedorenko.bookstore.repository.UserRepository;
import maksym.fedorenko.bookstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Registration was failed");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(getDefaultRoles());
        return userMapper.toDto(userRepository.save(user));
    }

    private Set<Role> getDefaultRoles() {
        return new HashSet<>(Collections.singletonList(
                repository.findByName(RoleName.ROLE_USER)
        ));
    }
}
