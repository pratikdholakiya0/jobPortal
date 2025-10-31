package com.example.jobportal.auth.service;

import com.example.jobportal.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class JobPortalUserPrincipal implements UserDetails {

    private final String userId;
    private final String profileId;
    private final String resumeId;
    private final String companyId;
    private final String username;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public JobPortalUserPrincipal(
            String userId,
            String profileId,
            String resumeId,
            String companyId,
            String username,
            String password,
            Role role,
            Collection<? extends GrantedAuthority> authorities
    ){
        this.userId = userId;
        this.profileId = profileId;
        this.resumeId = resumeId;
        this.companyId = companyId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}