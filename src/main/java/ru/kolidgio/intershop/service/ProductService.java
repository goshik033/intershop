package ru.kolidgio.intershop.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kolidgio.intershop.dto.product.CreateProductDto;
import ru.kolidgio.intershop.dto.product.UpdateProductDto;
import ru.kolidgio.intershop.model.Product;
import ru.kolidgio.intershop.repository.ProductRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;
import ru.kolidgio.intershop.service.errors.ConflictException;
import ru.kolidgio.intershop.service.errors.NotFoundException;

import java.math.BigDecimal;

@Validated
@Service
@RequiredArgsConstructor
public class ProductService {
    private final static BigDecimal MAX_PRICE = new BigDecimal("9999999999999999.99");
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product getOrThrow(Long id) {
        requireId(id, "productId");
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product  с id " + id + " не найден"));
    }

    @Transactional
    public Product createOrThrow(@Valid CreateProductDto dto) {
        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .imageUrl(dto.imageUrl())
                .build();
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать продукт из-за ограничения БД",e);
        }

    }


    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String q, BigDecimal min, BigDecimal max, Pageable pageable) {
        if (pageable == null) throw new BadRequestException("Pageable не должен быть null");

        BigDecimal effectiveMin = (min == null) ? BigDecimal.ZERO : min;
        BigDecimal effectiveMax = (max == null) ? MAX_PRICE : max;

        if (effectiveMin.compareTo(effectiveMax) > 0) {
            throw new BadRequestException("min не должен быть больше max");
        }

        String term = (q == null || q.isBlank()) ? null : q.trim();

        if (term == null) {
            return productRepository.findByPriceBetween(effectiveMin, effectiveMax, pageable);
        }

        return productRepository
                .findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        effectiveMin, effectiveMax, term, term, pageable);

    }

    @Transactional
    public Product updateOrThrow(Long id, @Valid UpdateProductDto dto) {
        requireId(id, "productId");
        Product product = getOrThrow(id);
        if (dto.name() != null) product.setName(dto.name());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.imageUrl() != null) product.setImageUrl(dto.imageUrl());
        try {
            return productRepository.save(product);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить продукт из-за ограничения БД",e);
        }
    }

    @Transactional
    public void deleteOrThrow(Long id) {
        requireId(id, "productId");
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить продукт из-за ограничения БД",e);
        }
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
