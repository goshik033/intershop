package ru.kolidgio.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolidgio.intershop.dto.user.LoginDto;
import ru.kolidgio.intershop.model.User;
import ru.kolidgio.intershop.repository.UserRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Long loginUserIdOrThrow(LoginDto dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new BadRequestException("Неверный email или пароль"));
        if (passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            return user.getId();
        } else {
            throw new BadRequestException("Неверный email или пароль");
        }
    }

}
