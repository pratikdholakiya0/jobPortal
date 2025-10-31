package com.example.jobportal.config;

import com.example.jobportal.filter.JwtFilter;
import com.example.jobportal.user.enums.Role;
import com.example.jobportal.user.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->req
                        // Public access for Auth endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/company/getCompany/**").permitAll()

                        // Employer Roles
                        .requestMatchers("/api/v1/company/**").hasRole(Role.EMPLOYER.name())
                        .requestMatchers("/api/v1/jobs/**").hasRole(Role.EMPLOYER.name())
                        .requestMatchers("/api/v1/applications/status/update", "/api/v1/applications/by-employer").hasRole(Role.EMPLOYER.name())

                        // Authenticated Public Job/User Access
                        .requestMatchers("/api/v1/jobs/getAllActive", "/api/v1/jobs/id").authenticated()
                        .requestMatchers("/user/**").authenticated()

                        // Applicant Roles
                        .requestMatchers("/api/v1/applications/**").hasRole(Role.APPLICANT.name())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e->e
                        // 1. Handles 401 Unauthorized (Invalid/Missing Token) - Prevents DB lookup fallbacks
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "status": 401,
                                  "error": "Unauthorized",
                                  "message": "Authentication token is missing or invalid.",
                                  "path": "%s"
                                }
                                """.formatted(request.getRequestURI()));
                        })
                        // 2. Handles 403 Forbidden (Insufficient Role/Permissions)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String message = accessDeniedException.getMessage();
                            if (accessDeniedException.getMessage().equals("Access Denied")) message = "You did not have permission to access this resource";

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "status": 403,
                                  "error": "Forbidden",
                                  "message": "%s",
                                  "path": "%s"
                                }
                                """.formatted(message ,request.getRequestURI()));
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}