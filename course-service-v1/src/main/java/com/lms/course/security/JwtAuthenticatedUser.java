package com.lms.course.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
public class JwtAuthenticatedUser {

    private final UUID userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtAuthenticatedUser(UUID userId, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

}
