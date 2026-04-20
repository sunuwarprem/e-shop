package com.prem.ecommerce.controller;

import com.prem.ecommerce.service.CategoryService;
import com.prem.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private void addAdminAttribute(Model model, Authentication authentication) {
        if (authentication != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("loggedInUser", authentication.getName());
        }
    }

    @GetMapping
    public String shopHome(Model model, Authentication authentication) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        addAdminAttribute(model, authentication);
        return "shop";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model,
                                 Authentication authentication) {
        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        addAdminAttribute(model, authentication);
        return "shop";
    }

    @GetMapping("/category/{id}")
    public String filterByCategory(@PathVariable Long id, Model model,
                                   Authentication authentication) {
        model.addAttribute("products", productService.getProductsByCategory(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategory", id);
        addAdminAttribute(model, authentication);
        return "shop";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(p ->
                model.addAttribute("product", p));
        return "product-detail";
    }
}