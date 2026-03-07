package ru.kolidgio.intershop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.intershop.model.Product;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByPriceBetweenAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            BigDecimal min, BigDecimal max,
            String q1, String q2,
            Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

}
