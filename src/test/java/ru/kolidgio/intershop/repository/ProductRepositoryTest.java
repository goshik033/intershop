package ru.kolidgio.intershop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.kolidgio.intershop.model.Product;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findByPriceBetween_filtersByPrice() {
        productRepository.save(Product.builder()
                .name("Cheap")
                .description("cheap item")
                .price(new BigDecimal("10.00"))
                .imageUrl("")
                .build());

        productRepository.save(Product.builder()
                .name("Mid")
                .description("mid item")
                .price(new BigDecimal("50.00"))
                .imageUrl("")
                .build());

        productRepository.save(Product.builder()
                .name("Expensive")
                .description("expensive item")
                .price(new BigDecimal("200.00"))
                .imageUrl("")
                .build());

        var page = productRepository.findByPriceBetween(
                new BigDecimal("20.00"),
                new BigDecimal("100.00"),
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getName()).isEqualTo("Mid");
    }

    @Test
    void findByPriceBetweenAndNameOrDescription_searchesIgnoreCase() {
        productRepository.save(Product.builder()
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("999.99"))
                .imageUrl("")
                .build());

        productRepository.save(Product.builder()
                .name("Samsung A")
                .description("Best PHONE ever")
                .price(new BigDecimal("799.99"))
                .imageUrl("")
                .build());

        var page = productRepository
                .findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        BigDecimal.ZERO, new BigDecimal("10000.00"),
                        "phone", "phone",
                        PageRequest.of(0, 10)
                );

        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
