package com.vinyl.VinylExchange.security.principal;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vinyl.VinylExchange.auth.enums.RoleName;
import com.vinyl.VinylExchange.user.User;
import com.vinyl.VinylExchange.user.UserStatus;

public class UserPrincipal implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {

        this.user = user;
        this.authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UUID getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    public UserStatus getUserStatus() {
        return user.getStatus();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Set<RoleName> getRoles() {

        return authorities
                .stream()
                .map(authority -> RoleName.valueOf(authority.getAuthority()))
                .collect(Collectors.toSet());
    }

    public boolean hasRole(RoleName roleName) {

        return authorities
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName.name()));
    }

}
