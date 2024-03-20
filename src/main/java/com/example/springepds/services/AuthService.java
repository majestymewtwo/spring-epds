package com.example.springepds.services;

import com.example.springepds.dto.JwtDTO;
import com.example.springepds.dto.LoginDTO;
import com.example.springepds.dto.RegisterDTO;
import com.example.springepds.models.User;
import com.example.springepds.repositories.UserRepository;
import com.example.springepds.security.JwtService;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${twilio.account.sid}")
    private String accSid;
    @Value("${twilio.auth.token}")
    private String twilioAuthToken;
    @Value("${twilio.service.sid}")
    private String serviceSid;

    public JwtDTO registerCustomer(RegisterDTO registerDTO) throws Exception {
        if(registerDTO.getPhone() != null) {
            Optional<User> check = userRepository.findByPhone(registerDTO.getPhone());
            if(check.isPresent()){
                throw new Exception("Account already exists");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new JwtDTO(jwtToken);
    }
    public String generateOTP(String phone) throws Exception {
        Twilio.init(accSid, twilioAuthToken);
        Verification verification = Verification.creator(
                serviceSid,
                "+91" + phone,
                "sms"
        ).create();
        return "Your OTP has been sent to your verified phone number";
    }

    public JwtDTO validateOTP(String otp, String phone) throws Exception {
        String jwtToken;
        Twilio.init(accSid, twilioAuthToken);
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            serviceSid)
                    .setTo("+91" + phone)
                    .setCode(otp)
                    .create();

            for (int i = 0; i < 8; i++) {
                if (verificationCheck.getStatus().equals("approved")) break;
                Thread.sleep(700);
            }
            if (!verificationCheck.getStatus().equals("approved"))
                throw new Exception("Invalid OTP");
        } catch (Exception e) {
            throw new Exception("Invalid OTP");
        }
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isPresent()) {
            jwtToken = jwtService.generateToken(user.get());
        } else {
            User newUser = User.builder()
                    .phone(phone).build();
            newUser.setRole("ROLE_CUSTOMER");
            userRepository.save(newUser);
            jwtToken = jwtService.generateToken(newUser);
        }
        return JwtDTO.builder()
                .token(jwtToken)
                .build();
    }
    public JwtDTO authenticateAdmin(LoginDTO loginDTO) throws Exception {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(
                () -> new RuntimeException("Customer not found")
        );
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                (loginDTO.getPassword())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtService.generateToken(authentication);
        return new JwtDTO(jwtToken);
    }

}
