package com.prem.ecommerce.service;

import com.prem.ecommerce.model.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartService {

    private List<CartItem> cartItems = new ArrayList<>();

    public void addToCart(CartItem item) {
        Optional<CartItem> existing = cartItems.stream()
                .filter(c -> c.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(
                    existing.get().getQuantity() + item.getQuantity());
        } else {
            cartItems.add(item);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(c -> c.getProductId().equals(productId));
    }

    public void updateQuantity(Long productId, int quantity) {
        cartItems.stream()
                .filter(c -> c.getProductId().equals(productId))
                .findFirst()
                .ifPresent(c -> c.setQuantity(quantity));
    }

    public double getTotal() {
        return cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getCartCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}