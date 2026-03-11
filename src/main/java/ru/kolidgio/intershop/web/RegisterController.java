package ru.kolidgio.intershop.web;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kolidgio.intershop.dto.user.CreateUserDto;
import ru.kolidgio.intershop.model.User;
import ru.kolidgio.intershop.service.UserService;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new CreateUserDto("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") @Valid CreateUserDto dto,
                           BindingResult bindingResult,
                           HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User created = userService.createOrThrow(dto);
        session.setAttribute(AuthController.SESSION_USER_ID, created.getId());

        return "redirect:/products";
    }
}