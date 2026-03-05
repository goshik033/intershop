package ru.kolidgio.intershop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kolidgio.intershop.model.Product;
import ru.kolidgio.intershop.repository.ProductRepository;

import java.math.BigDecimal;

@Configuration
public class DataInitConfig {

    @Bean
    CommandLineRunner initProducts(ProductRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            repo.save(Product.builder()
                    .name("Товар 1")
                    .description("Описание товара 1")
                    .price(new BigDecimal("1990.00"))
                    .imageUrl("https://via.placeholder.com/200")
                    .build());

            repo.save(Product.builder()
                    .name("Товар 2")
                    .description("Описание товара 2")
                    .price(new BigDecimal("4990.00"))
                    .imageUrl("https://via.placeholder.com/200")
                    .build());

            repo.save(Product.builder()
                    .name("Товар 3")
                    .description("Описание товара 3")
                    .price(new BigDecimal("990.00"))
                    .imageUrl("https://via.placeholder.com/200")
                    .build());
        };
    }
}