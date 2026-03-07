package ru.kolidgio.intershop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record CreateProductDto(
        @NotNull @Size(max = 255) String name,
        @NotBlank @Size(max = 4000) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Size(max=1024) String imageUrl
) {
}
