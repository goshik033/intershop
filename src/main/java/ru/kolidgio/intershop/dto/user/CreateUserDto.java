package ru.kolidgio.intershop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  CreateUserDto(
        @NotBlank @Size(min = 8, max = 50) String username,
        @NotBlank @Email @Size(max = 320)  String email,
        @NotBlank  @Size(min = 8, max = 72) String password
        ) {
}
