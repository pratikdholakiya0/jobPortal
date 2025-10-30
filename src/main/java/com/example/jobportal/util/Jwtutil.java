package com.example.jobportal.util;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.candidate.entity.Resume;
import com.example.jobportal.candidate.repository.ResumeRepository;
import com.example.jobportal.company.entity.Company;
import com.example.jobportal.company.repository.CompanyRepository;
import com.example.jobportal.user.entity.Profile;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.ProfileRepository;
import com.example.jobportal.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Jwtutil {

    @Value("${application.security.jwt.secret-key}")
    private String JWT_SECRET;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isValid(String token){
        return !isExpired(token);
    }

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }
}
