package com.example.springepds.services;

import com.example.springepds.config.AuthContext;
import com.example.springepds.dto.CartItemDTO;
import com.example.springepds.dto.TransactionDTO;
import com.example.springepds.models.*;
import com.example.springepds.repositories.CartRepository;
import com.example.springepds.repositories.ProductRepository;
import com.example.springepds.repositories.TransactionRepository;
import com.example.springepds.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    public Product createNewProduct(Product newProduct){
        return productRepository.save(newProduct);
    }
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    @Transactional
    public void addToCart(Long productId) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new Exception("Product not found")
        );
        Cart cart;
        User user = userRepository.findByPhone(AuthContext.getCurrentUser()).orElseThrow(
                () -> new Exception("User not found")
        );
        Optional<Cart> item = cartRepository.findByUserPhoneAndProductId(user.getPhone(), productId);
        if(item.isPresent()) {
            cart = item.get();
            cart.setQuantity(cart.getQuantity() + 1);
        }else {
            cart = new Cart();
            cart.setQuantity(1);
            cart.setProduct(product);
            cart.setUser(user);
        }
        user.getCart().add(cartRepository.save(cart));
    }
    public List<CartItemDTO> getCartItems() throws Exception {
        List<Cart> cartList = cartRepository.findAllByUserPhone(AuthContext.getCurrentUser());
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for(Cart cart : cartList) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setQuantity(cart.getQuantity());
            cartItemDTO.setProductId(cart.getProduct().getId());
            BeanUtils.copyProperties(cart.getProduct(), cartItemDTO);
            cartItemDTO.setId(cart.getId());
            cartItemDTOS.add(cartItemDTO);
        }
        return cartItemDTOS;
    }
    public void updateCart(Cart cartDetails) {
        cartDetails.setUser(userRepository.findByPhone(AuthContext.getCurrentUser()).orElseThrow(
                () -> new RuntimeException("Customer not found")
        ));
        if(cartDetails.getQuantity() == 0) {
            cartRepository.delete(cartDetails);
        } else {
            cartRepository.save(cartDetails);
        }
    }
    public void removeFromCart(Long cartId) throws Exception {
        cartRepository.deleteById(cartId);
    }
    public void initiateTransaction() throws Exception {
        User user = userRepository.findByPhone(AuthContext.getCurrentUser()).orElseThrow(
                () -> new Exception("User not found")
        );
        List<Cart> cartList = cartRepository.findAllByUserPhone(AuthContext.getCurrentUser());
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCreatedAt(new Date());
        transaction.setIsDispatched(false);
        transaction.setIsDelivered(false);
        List<OrderItem> orderItems = new ArrayList<>();
        for(Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTransaction(transaction);
            orderItems.add(orderItem);
            cartRepository.delete(cartItem);
        }
        transaction.setOrderItems(orderItems);
        transactionRepository.save(transaction);
    }
    public List<TransactionDTO> getOrders() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        for(Transaction transaction : transactions) {
            TransactionDTO transactionDTO = mapToDTO(transaction);
            transactionDTOS.add(transactionDTO);
        }
        return transactionDTOS;
    }
    public void updateTransactionStatus(Long id, Integer code) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if(code.equals(1)) {
            transaction.setIsDispatched(true);
        }
        else if(code.equals(-1)) {
            transaction.setIsDelivered(true);
        }
    }
    public List<TransactionDTO> getHistoryOfOrders() {
        User user = userRepository.findByPhone(AuthContext.getCurrentUser()).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        return transactionRepository.findAllByUserId(user.getId())
                .stream().map(this::mapToDTO).toList();
    }
    public TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setCreatedAt(transaction.getCreatedAt());
        transactionDTO.setId(transaction.getId());
        transactionDTO.setIsDispatched(transaction.getIsDispatched());
        transactionDTO.setIsDelivered(transaction.getIsDelivered());
        transactionDTO.setClientPhone(transaction.getUser().getPhone());
        List<CartItemDTO> orderDTOS = new ArrayList<>();
        for(OrderItem orderItem : transaction.getOrderItems()) {
            CartItemDTO orderDTO = new CartItemDTO();
            BeanUtils.copyProperties(orderItem.getProduct(), orderDTO);
            orderDTO.setQuantity(orderItem.getQuantity());
            orderDTOS.add(orderDTO);
        }
        transactionDTO.setOrderItems(orderDTOS);
        return transactionDTO;
    }
}
