package ru.kolidgio.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.intershop.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
