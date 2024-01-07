package com.example.springepds.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transaction_product")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    @ManyToOne
    private Product product;
    @ManyToOne
    private Transaction transaction;
}
