package com.example.jobportal.auth.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class JobPortalUserPrincipal implements UserDetails {

    private final String userId;
    private final String profileId;
    private final String candidateProfileId;
    private final String companyId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public JobPortalUserPrincipal(
            String userId,
            String profileId,
            String candidateProfileId,
            String companyId,
            String username,
            Collection<? extends GrantedAuthority> authorities
    ){
        this.userId = userId;
        this.profileId = profileId;
        this.candidateProfileId = candidateProfileId;
        this.companyId = companyId;
        this.username = username;
        this.password = null;
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