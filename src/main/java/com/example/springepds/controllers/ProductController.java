package com.example.springepds.controllers;

import com.example.springepds.dto.CartItemDTO;
import com.example.springepds.dto.TransactionDTO;
import com.example.springepds.models.Cart;
import com.example.springepds.models.Product;
import com.example.springepds.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    @GetMapping("/all")
    public List<Product> allProducts(){
        return productService.getAllProducts();
    }
    @PostMapping("/addToCart/{productId}")
    public ResponseEntity<String> addToCart(@PathVariable Long productId) throws Exception {
        productService.addToCart(productId);
        return ResponseEntity.ok("Added to cart");
    }
    @GetMapping("/getCart")
    public List<CartItemDTO> getCartItems() throws Exception {
        return productService.getCartItems();
    }
    @PostMapping("/updateCart")
    public ResponseEntity<String> updateCart(@RequestBody Cart cart) {
        productService.updateCart(cart);
        return ResponseEntity.ok("Updated Cart");
    }
    @DeleteMapping("/removeFromCart/{cartId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long cartId) throws Exception {
        productService.removeFromCart(cartId);
        return ResponseEntity.ok("Removed From Cart");
    }
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutCart() throws Exception {
        productService.initiateTransaction();
        return ResponseEntity.ok("Checked out successfully");
    }
    @GetMapping("/orderHistory")
    public List<TransactionDTO> getOrderHistory() {
        return productService.getHistoryOfOrders();
    }
}
