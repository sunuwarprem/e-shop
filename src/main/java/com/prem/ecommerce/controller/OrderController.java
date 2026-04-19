package com.prem.ecommerce.controller;

import com.prem.ecommerce.model.Order;
import com.prem.ecommerce.model.OrderItem;
import com.prem.ecommerce.model.User;
import com.prem.ecommerce.service.CartService;
import com.prem.ecommerce.service.OrderService;
import com.prem.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
                             @RequestParam String shippingAddress) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(BigDecimal.valueOf(cartService.getTotal()));

        List<OrderItem> items = cartService.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setQuantity(cartItem.getQuantity());
                    item.setPrice(BigDecimal.valueOf(cartItem.getPrice()));
                    return item;
                }).collect(Collectors.toList());

        order.setItems(items);
        Order savedOrder = orderService.saveOrder(order);
        cartService.clearCart();

        return "redirect:/order/confirmation/" + savedOrder.getId();
    }

    @GetMapping("/confirmation/{id}")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        orderService.getOrderById(id).ifPresent(order ->
                model.addAttribute("order", order));
        return "order-confirmation";
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "my-orders";
    }
}