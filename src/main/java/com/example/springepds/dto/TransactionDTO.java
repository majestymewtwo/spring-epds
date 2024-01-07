package com.example.springepds.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransactionDTO {
    private Date createdAt;
    private List<CartItemDTO> orderItems;
}
