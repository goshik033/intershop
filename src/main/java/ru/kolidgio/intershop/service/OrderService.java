package ru.kolidgio.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolidgio.intershop.model.CartItem;
import ru.kolidgio.intershop.model.Order;
import ru.kolidgio.intershop.model.OrderItem;
import ru.kolidgio.intershop.model.User;
import ru.kolidgio.intershop.repository.OrderItemRepository;
import ru.kolidgio.intershop.repository.OrderRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;
import ru.kolidgio.intershop.service.errors.ConflictException;
import ru.kolidgio.intershop.service.errors.NotFoundException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserService userService;

    @Transactional
    public Order checkoutOrThrow(Long userId) {
        User user = userService.getOrThrow(userId);

        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Корзина пустая");
        }

        Order order = Order.builder()
                .user(user)
                .createdAt(Instant.now())
                .build();

        try {
            order = orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать заказ из-за ограничения БД", e);
        }

        try {
            for (CartItem ci : cartItems) {
                OrderItem oi = OrderItem.builder()
                        .order(order)
                        .product(ci.getProduct())
                        .quantity(ci.getQuantity())
                        .priceAtPurchase(ci.getProduct().getPrice())
                        .build();
                orderItemRepository.save(oi);
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать позиции заказа из-за ограничения БД", e);
        }

        cartService.deleteAll(userId);
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(Long userId) {
        userService.getOrThrow(userId);
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Order getOrderOrThrow(Long userId, Long orderId) {
        userService.getOrThrow(userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new NotFoundException("Заказ не найден: " + orderId);
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long userId, Long orderId) {
        getOrderOrThrow(userId, orderId);
        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getOrderTotal(Long userId, Long orderId) {
        return getOrderItems(userId, orderId).stream()
                .map(oi -> oi.getPriceAtPurchase().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAllOrdersTotal(Long userId) {
        return getOrders(userId).stream()
                .map(o -> getOrderTotal(userId, o.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}