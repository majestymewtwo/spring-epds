package com.example.springepds.controllers;

import com.example.springepds.dto.TransactionDTO;
import com.example.springepds.models.Product;
import com.example.springepds.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
    private final ProductService productService;
    @GetMapping("/getOrders")
    public List<TransactionDTO> getOrders() {
        return productService.getOrders();
    }
    @PostMapping("/new")
    public Product newProduct(@RequestBody Product newProduct){
        return productService.createNewProduct(newProduct);
    }
    @PostMapping("/dispatchOrder/{id}")
    public ResponseEntity<String> dispatchOrder(@PathVariable Long id) {
        productService.updateTransactionStatus(id, 1);
        return ResponseEntity.ok("Order successfully dispatched");
    }
    @PostMapping("/deliverStatusUpdate/{id}")
    public ResponseEntity<String> deliverStatusUpdate(@PathVariable Long id) {
        productService.updateTransactionStatus(id, 1);
        return ResponseEntity.ok("Order successfully delivered");
    }
}
