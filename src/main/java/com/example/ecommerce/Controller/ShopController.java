package com.example.ecommerce.controller;

import com.example.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShopController {

    private final ProductService productService;

    public ShopController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String viewShop(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("cart", productService.getCart());
        model.addAttribute("total", productService.getCartTotal());
        return "index";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam String id) {
        productService.addToCart(id);
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(Model model) {
        double total = productService.getCartTotal();
        productService.clearCart();
        model.addAttribute("message", "Success! Order processed. Total Paid: $" + String.format("%.2f", total));
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("cart", productService.getCart());
        model.addAttribute("total", 0.0);
        return "index";
    }
}
