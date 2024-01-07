package com.example.springepds.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransactionDTO {
    private Long id;
    private Date createdAt;
    private String clientPhone;
    private List<CartItemDTO> orderItems;
}
