package ru.kolidgio.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolidgio.intershop.dto.user.CreateUserDto;
import ru.kolidgio.intershop.dto.user.UpdateUserDto;
import ru.kolidgio.intershop.model.User;
import ru.kolidgio.intershop.model.UserRole;
import ru.kolidgio.intershop.repository.UserRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;
import ru.kolidgio.intershop.service.errors.ConflictException;
import ru.kolidgio.intershop.service.errors.NotFoundException;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createOrThrow(CreateUserDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new ConflictException("Email уже занят");
        }
        if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
            throw new ConflictException("Username уже занят");
        }
        User user = User.builder()
                .email(dto.email())
                .username(dto.username())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(UserRole.USER)
                .build();
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать пользователя из-за ограничения БД", e);
        }

    }

    @Transactional
    public User updateOrThrow(Long id, UpdateUserDto dto) {
        requireId(id, "userId");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User  с id " + id + " не найден"));
        if (dto.email() != null) {
            if (userRepository.existsByEmailIgnoreCase(dto.email())) {
                throw new NotFoundException("User с email " + dto.email() + " уже существует");
            }
            user.setEmail(dto.email());
        }
        if (dto.username() != null) {
            if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
                throw new NotFoundException("User с username " + dto.username() + " уже существует");
            }
            user.setUsername(dto.username());
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить пользователя из-за ограничения БД", e);

        }
    }

    @Transactional
    public void deleteOrThrow(Long id) {
        requireId(id, "userId");
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить пользователя из-за ограничения БД", e);
        }
    }

    @Transactional(readOnly = true)
    public User getOrThrow(Long id) {
        requireId(id, "userId");
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User  с id " + id + " не найден"));

    }


    private void requireId(Long id, String field) {
        if (id == null) {
            throw new BadRequestException(field + " не должен быть null");
        }
        if (id < 1) {
            throw new BadRequestException(field + " не должен быть меньше 1");
        }
    }
}
