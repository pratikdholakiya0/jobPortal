package com.example.jobportal.auth.controller;

import com.example.jobportal.auth.dto.request.LoginRequest;
import com.example.jobportal.auth.dto.response.LoginResponse;
import com.example.jobportal.auth.dto.request.RegisterRequest;
import com.example.jobportal.auth.dto.response.RegisterResponse;
import com.example.jobportal.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        RegisterResponse registerResponse = RegisterResponse.builder()
                .email(registerRequest.getEmail())
                .message("Account has been created successfully").build();
        return new ResponseEntity<>(registerResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("User logged in successfully")
                .token(token).build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
