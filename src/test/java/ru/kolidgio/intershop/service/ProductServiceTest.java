package ru.kolidgio.intershop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.kolidgio.intershop.repository.ProductRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    void throws_whenMinGreaterThanMax() {
        var pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() ->
                productService.searchProducts("x",
                        new BigDecimal("10.00"),
                        new BigDecimal("5.00"),
                        pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("min");
    }

    @Test
    void whenQueryBlank_callsFindByPriceBetween() {
        var pageable = PageRequest.of(0, 10);
        Page<?> empty = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findByPriceBetween(any(), any(), eq(pageable)))
                .thenReturn((Page) empty);

        productService.searchProducts("   ", null, null, pageable);

        verify(productRepository).findByPriceBetween(any(BigDecimal.class), any(BigDecimal.class), eq(pageable));
        verify(productRepository, never())
                .findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any(), any(), any(), any());
    }

    @Test
    void whenQueryPresent_callsNameOrDescriptionSearch() {
        var pageable = PageRequest.of(0, 10);
        Page<?> empty = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository
                .findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        any(), any(), eq("phone"), eq("phone"), eq(pageable)))
                .thenReturn((Page) empty);

        productService.searchProducts(" phone ", null, null, pageable);

        verify(productRepository, never()).findByPriceBetween(any(), any(), any());
        verify(productRepository)
                .findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        any(BigDecimal.class), any(BigDecimal.class), eq("phone"), eq("phone"), eq(pageable));
    }
}