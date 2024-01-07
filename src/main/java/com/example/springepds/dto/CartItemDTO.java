package com.example.springepds.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    public int quantity;
    private String name;
    private String image;
    private Double price;
    private String scale;
}
