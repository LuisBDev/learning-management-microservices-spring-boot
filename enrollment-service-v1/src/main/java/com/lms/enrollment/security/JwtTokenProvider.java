package com.lms.enrollment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
    }

    public Optional<JwtAuthenticatedUser> validateAndExtract(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("token_type", String.class);
            if (!"access".equals(tokenType)) {
                log.warn("Rejected non-access token");
                return Optional.empty();
            }

            if (claims.getExpiration().before(new Date())) {
                log.warn("Rejected expired token");
                return Optional.empty();
            }

            String email = claims.getSubject();
            String userIdStr = claims.get("user_id", String.class);
            UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles != null) {
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
            }

            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);
            if (permissions != null) {
                permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
            }

            return Optional.of(
                    JwtAuthenticatedUser.builder()
                            .userId(userId)
                            .email(email)
                            .authorities(authorities)
                            .build()
            );

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
