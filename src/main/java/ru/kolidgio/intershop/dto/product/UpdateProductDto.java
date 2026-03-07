package ru.kolidgio.intershop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductDto(
        @Size(max = 255) String name,
        @Size(max = 4000) String description,
        @DecimalMin("0.01") BigDecimal price,
        @Size(max = 1024) String imageUrl
) {
}

