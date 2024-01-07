package com.example.springepds.controllers;

import com.example.springepds.dto.JwtDTO;
import com.example.springepds.dto.LoginDTO;
import com.example.springepds.dto.RegisterDTO;
import com.example.springepds.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public JwtDTO registerUser(@RequestBody RegisterDTO registerDTO) throws Exception {
        return authService.registerCustomer(registerDTO);
    }
    @PostMapping("/loginAdmin")
    public JwtDTO authenticateAdmin(@RequestBody LoginDTO loginDTO) throws Exception {
        return authService.authenticateAdmin(loginDTO);
    }
    @PostMapping("/getOTP/{phone}")
    public String getOTP(@PathVariable String phone) throws Exception {
        return authService.generateOTP(phone);
    }
    @PostMapping("/validateOTP/{phone}&&{otp}")
    public JwtDTO validateOTP(@PathVariable String otp, @PathVariable String phone) throws Exception {
        return authService.validateOTP(otp, phone);
    }
}
