package ru.kolidgio.intershop.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kolidgio.intershop.service.ProductService;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String q,
                           @RequestParam(required = false) BigDecimal min,
                           @RequestParam(required = false) BigDecimal max,
                           Pageable pageable,
                           Model model) {

        model.addAttribute("page", productService.searchProducts(q, min, max, pageable));
        model.addAttribute("q", q);
        model.addAttribute("min", min);
        model.addAttribute("max", max);

        return "products";
    }

    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable("id") Long productId, Model model) {
        model.addAttribute("product", productService.getOrThrow(productId));
        return "product";
    }
}
