package com.example.jobportal.config;

import com.example.jobportal.filter.JwtFilter;
import com.example.jobportal.user.enums.Role;
import com.example.jobportal.user.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/api/v1/company/getCompany/id").authenticated()
                            .requestMatchers("/api/v1/company/**").hasRole(Role.EMPLOYER.name())
                            .requestMatchers("/api/v1/jobs/getAllActive", "api/v1/jobs/id").authenticated()
                            .requestMatchers("/api/v1/jobs/**").hasRole(Role.EMPLOYER.name())
                            .requestMatchers("/api/v1/applications/status/update", "/api/v1/applications/by-employer").hasRole(Role.EMPLOYER.name())
                            .requestMatchers("/api/v1/applications/**").hasRole(Role.APPLICANT.name())
                            .requestMatchers("/user/**").authenticated()
                            .anyRequest().authenticated()
                    )
                .exceptionHandling(e->e
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Handles 403 (forbidden)
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "status": 403,
                                  "error": "Forbidden",
                                  "message": "Access denied: You do not have permission to perform this action.",
                                  "path": "%s"
                                }
                                """.formatted(request.getRequestURI()));
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
