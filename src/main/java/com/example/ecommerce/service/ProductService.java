package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    public ProductService() {
        // Seed the exact products requested
        products.add(new Product("p1", "iPhone 15 Pro", "Mobiles", 999.99, 15));
        products.add(new Product("p2", "Classic Graphic Tee", "T-shirts", 19.99, 100));
        products.add(new Product("p3", "Slim Fit Denim Jeans", "Jeans", 49.99, 50));
        products.add(new Product("p4", "USB-C Fast Charger Wire (2m)", "Charger wire", 12.49, 200));
        products.add(new Product("p5", "Wireless Noise-Cancelling Headphones", "Headphones", 199.99, 30));
        products.add(new Product("p6", "128GB High-Speed USB Flash Drive", "USB", 15.99, 150));
        products.add(new Product("p7", "55-inch 4K Ultra HD Smart Television", "Television", 449.99, 10));
        products.add(new Product("p8", "Dolby Atmos Surround Sound bar", "Sound bar", 129.99, 25));
        products.add(new Product("p9", "Next-Gen M-Series 16-inch Laptop", "Laptops", 1299.99, 8));
        products.add(new Product("10", "Xbox Series X Console", "Xbox", 499.99, 12));
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public void addToCart(String productId) {
        Optional<Product> productOpt = products.stream().filter(p -> p.getId().equals(productId)).findFirst();
        if (productOpt.isPresent()) {
            Product prod = productOpt.get();
            if (prod.getStock() > 0) {
                prod.setStock(prod.getStock() - 1);
                
                // Add or increment in cart
                Optional<CartItem> existingItem = cart.stream()
                        .filter(item -> item.getProduct().getId().equals(productId))
                        .findFirst();
                if (existingItem.isPresent()) {
                    existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
                } else {
                    cart.add(new CartItem(prod, 1));
                }
            }
        }
    }

    public double getCartTotal() {
        return cart.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    public void clearCart() {
        cart.clear();
    }
}
