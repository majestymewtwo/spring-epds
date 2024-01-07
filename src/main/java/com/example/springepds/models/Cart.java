package com.example.springepds.models;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Product product;
    private int quantity;
}
