package com.prem.ecommerce.controller;

import com.prem.ecommerce.model.CartItem;
import com.prem.ecommerce.service.CartService;
import com.prem.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("cartCount", cartService.getCartCount());
        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity) {
        productService.getProductById(productId).ifPresent(product -> {
            CartItem item = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice().doubleValue(),
                    quantity,
                    product.getImageUrl()
            );
            cartService.addToCart(item);
        });
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity,
                                 Model model) {
        if (quantity <= 0) {
            cartService.removeFromCart(productId);
        } else {
            // Check stock before updating
            productService.getProductById(productId).ifPresent(product -> {
                if (quantity <= product.getStock()) {
                    cartService.updateQuantity(productId, quantity);
                } else {
                    cartService.updateQuantity(productId, product.getStock());
                }
            });
        }
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}