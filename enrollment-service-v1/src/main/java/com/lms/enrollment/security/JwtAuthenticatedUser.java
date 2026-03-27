package com.lms.enrollment.security;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class JwtAuthenticatedUser {

    private final UUID userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;


}
