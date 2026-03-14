package ru.kolidgio.intershop.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kolidgio.intershop.model.Order;
import ru.kolidgio.intershop.service.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(AuthController.SESSION_USER_ID);
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpSession session) {

        if (currentUserId(session) == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrders(currentUserId(session)));
        model.addAttribute("total", orderService.getAllOrdersTotal(currentUserId(session)));

        return "orders";


    }

    @GetMapping("/orders/{orderId}")

    public String order(Model model, HttpSession session, @PathVariable("orderId") Long orderId) {
        if (currentUserId(session) == null) {
            return "redirect:/login";
        }
        model.addAttribute("order", orderService.getOrderOrThrow(currentUserId(session), orderId));
        model.addAttribute("total", orderService.getOrderTotal(currentUserId(session), orderId));
        model.addAttribute("items", orderService.getOrderItems(currentUserId(session), orderId));
        return "order";
    }

    @PostMapping("/orders/checkout")
    public String checkout(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return "redirect:/login";

        Order order = orderService.checkoutOrThrow(userId);
        return "redirect:/orders/" + order.getId();
    }
}
