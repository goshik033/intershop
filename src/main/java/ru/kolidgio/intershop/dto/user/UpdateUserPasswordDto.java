package ru.kolidgio.intershop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserPasswordDto(
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
