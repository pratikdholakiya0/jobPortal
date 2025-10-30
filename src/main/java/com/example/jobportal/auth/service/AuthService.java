package com.example.jobportal.auth.service;

import com.example.jobportal.auth.dto.request.LoginRequest;
import com.example.jobportal.auth.dto.request.RegisterRequest;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import com.example.jobportal.util.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Jwtutil jwtutil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public void register(RegisterRequest registerRequest) {
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getUserRole())
                .creationDate(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        }catch (Exception e){
            throw new UsernameNotFoundException("Invalid email or password");
        }
        return jwtutil.generateToken(loginRequest.getEmail());
    }
}
