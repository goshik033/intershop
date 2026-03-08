package ru.kolidgio.intershop.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kolidgio.intershop.dto.product.CreateProductDto;
import ru.kolidgio.intershop.dto.product.UpdateProductDto;
import ru.kolidgio.intershop.model.Product;
import ru.kolidgio.intershop.service.ProductService;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String products(@PageableDefault(size = 20, sort = "name")
                           @RequestParam(required = false) String q,
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

    @GetMapping("/products/{productId}")
    public String getProduct(@PathVariable("productId") Long productId, Model model) {
        model.addAttribute("product", productService.getOrThrow(productId));
        return "product";
    }

    @GetMapping("/products/new")
    public String getProductForm(Model model) {
        model.addAttribute("form", new CreateProductDto("", "", null, ""));
        return "product-new";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute("form") @Valid CreateProductDto dto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "product-new";
        }
        Product created = productService.createOrThrow(dto);
        return "redirect:/products/" + created.getId();
    }

    @GetMapping("/products/{productId}/edit")
    public String editForm(@PathVariable("productId") Long productId, Model model) {
        Product p = productService.getOrThrow(productId);

        model.addAttribute("productId", productId);
        model.addAttribute("form", new UpdateProductDto(
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl()
        ));

        return "product-edit";
    }

    @PostMapping("/products/{productId}")
    public String update(@PathVariable("productId") Long productId,
                         @ModelAttribute("form") @Valid UpdateProductDto dto,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", productId);
            return "product-edit";
        }

        productService.updateOrThrow(productId, dto);
        return "redirect:/products/" + productId;
    }

    @PostMapping("/products/{productId}/delete")
    public String delete(@PathVariable("productId") Long productId) {
        productService.deleteOrThrow(productId);
        return "redirect:/products";
    }


}
