package ru.kolidgio.intershop.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;
import ru.kolidgio.intershop.service.CartService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerTest {

    private MockMvc mockMvc;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);

        ViewResolver resolver = (viewName, locale) -> {
            if (viewName != null && viewName.startsWith("redirect:")) {
                String target = viewName.substring("redirect:".length());
                return new RedirectView(target, true); // true = contextRelative
            }
            return (View) (model, request, response) -> response.setStatus(200);
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CartController(cartService))
                .setViewResolvers(resolver)
                .build();
    }

    @Test
    void cart_redirectsToLogin_whenNoSession() throws Exception {
        mockMvc.perform(get("/cart").session(new MockHttpSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void cart_renders_whenSessionPresent() throws Exception {
        Long userId = 1L;
        when(cartService.getCartItems(userId)).thenReturn(List.of());
        when(cartService.getTotal(userId)).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/cart")
                        .sessionAttr(AuthController.SESSION_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));

        verify(cartService).getCartItems(userId);
        verify(cartService).getTotal(userId);
    }
}