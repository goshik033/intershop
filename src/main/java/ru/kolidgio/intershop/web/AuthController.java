package ru.kolidgio.intershop.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kolidgio.intershop.dto.user.LoginDto;
import ru.kolidgio.intershop.service.AuthService;

@RequiredArgsConstructor
@Controller
public class AuthController {
    public static final String SESSION_USER_ID = "userId";
    private final AuthService authService;

    @GetMapping("/login")
    public String loginFrom(Model model) {
        model.addAttribute("form", new LoginDto("", ""));
        return "login";

    }

    @PostMapping("/login")
    public String login(@ModelAttribute("form") @Valid LoginDto dto, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        Long userId = authService.loginUserIdOrThrow(dto);
        session.setAttribute(SESSION_USER_ID, userId);
        return "redirect:/products";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
