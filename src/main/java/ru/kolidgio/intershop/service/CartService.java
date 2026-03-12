package ru.kolidgio.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolidgio.intershop.model.CartItem;
import ru.kolidgio.intershop.model.Product;
import ru.kolidgio.intershop.model.User;
import ru.kolidgio.intershop.repository.CartItemRepository;
import ru.kolidgio.intershop.service.errors.BadRequestException;
import ru.kolidgio.intershop.service.errors.ConflictException;
import ru.kolidgio.intershop.service.errors.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public CartItem addOrThrow(Long userId, Long productId) {
        User user = userService.getOrThrow(userId);
        Product product = productService.getOrThrow(productId);
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build()
                );

        cartItem.setQuantity(cartItem.getQuantity() + 1);

        try {
            return cartItemRepository.save(cartItem);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось добавить продукт в корзину из-за ограничения БД", e);
        }

    }

    @Transactional(readOnly = true)
    public BigDecimal getTotal(Long userId) {
        userService.getOrThrow(userId);
        return cartItemRepository.findAllByUserId(userId).stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        userService.getOrThrow(userId);
        return cartItemRepository.findAllByUserId(userId);
    }


    @Transactional
    public void changeQty(Long userId, Long productId, int qty) {

        if (qty < 0) {
            throw new BadRequestException("qty не может быть отрицательным");
        }

        User user = userService.getOrThrow(userId);
        Product product = productService.getOrThrow(productId);

        if (qty == 0) {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            return;
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build());

        item.setQuantity(qty);

        try {
            cartItemRepository.save(item);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить количество из-за ограничения БД", e);
        }
    }

    @Transactional
    public void delete(Long userId, Long productId) {
        userService.getOrThrow(userId);
        productService.getOrThrow(productId);
        try {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить продукт из корзины из-за ограничения БД", e);
        }
    }

    @Transactional
    public void deleteAll(Long userId) {
        userService.getOrThrow(userId);
        try {
            cartItemRepository.deleteAllByUserId(userId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить продукт из корзины из-за ограничения БД", e);
        }
    }

    @Transactional(readOnly = true)
    public CartItem getCartItem(Long id) {
        requireId(id, "cartItemId");
        return cartItemRepository.findById(id).orElseThrow(() -> new NotFoundException(("CartItem  с id " + id + " не найден")));

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
