package ru.kolidgio.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.intershop.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}