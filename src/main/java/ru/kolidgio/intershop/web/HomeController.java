package ru.kolidgio.intershop.web;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    @GetMapping("/")
    public String home(){
        return "index";
    }
}
