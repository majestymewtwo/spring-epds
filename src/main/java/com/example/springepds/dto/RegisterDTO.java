package com.example.springepds.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
    private String street;
    private String city;
    private String state;
    private String country;
    private String role;
}
