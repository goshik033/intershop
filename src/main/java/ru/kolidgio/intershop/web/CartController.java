package ru.kolidgio.intershop.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kolidgio.intershop.service.CartService;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(AuthController.SESSION_USER_ID);
    }

    @GetMapping("/cart")
    public String getAll(HttpSession session, Model model) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";
        model.addAttribute("items", cartService.getCartItems(userId));
        model.addAttribute("total", cartService.getTotal(userId));
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String add(@RequestHeader(value = "Referer", required = false) String referer, @PathVariable Long productId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.addOrThrow(userId, productId);
        return "redirect:" + (referer != null ? referer : "/products");
    }

    @PostMapping("/cart/qty/{productId}")
    public String changeQty(@PathVariable Long productId,
                            @RequestParam int qty,
                            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.changeQty(userId, productId, qty);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String remove(@PathVariable Long productId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";
        cartService.delete(userId, productId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clear(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";
        cartService.deleteAll(userId);
        return "redirect:/cart";
    }

}
